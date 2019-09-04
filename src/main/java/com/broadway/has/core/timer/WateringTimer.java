package com.broadway.has.core.timer;

import com.broadway.has.core.commander.Commander;
import com.broadway.has.core.commander.WateringRequest;
import com.broadway.has.core.repositories.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WateringTimer {

    private static final Logger logger = LoggerFactory.getLogger(WateringTimer.class);

    @Autowired
    WateringScheduleRepository wateringScheduleRepository;

    @Autowired
    RunHistoryRepository runHistoryRepository;

    @Autowired
    DelayWateringRepository delayWateringRepository;

    @Autowired
    Commander xbeeCommander;

    public static boolean fuzzyDoesTimestampMatch(int hoursFuzzinessAllowance, DateTime current, ScheduleDao scheduled){

        int dayOfWeek = current.getDayOfWeek();

        if(scheduled.getDayOfWeek() == current.getDayOfWeek()) {

            int minStartHour = scheduled.getHourOfDay();
            int maxStartHour = scheduled.getHourOfDay() + hoursFuzzinessAllowance;
            if(maxStartHour > 24) {
                maxStartHour = maxStartHour - 24;
            }

            if(minStartHour <= current.getHourOfDay() && current.getHourOfDay() <= maxStartHour) {
                return true;
            }
        }

        return false;


    }

    @Scheduled(fixedRate = 60000)
    public void checkForWatering(){

        //get the current day & time
        DateTime date = DateTime.now();

        int dayOfWeek = 3;

        //pull schedule out of database for all schedules today
        List<ScheduleDao> schedules = wateringScheduleRepository.findAllByDayOfWeek(dayOfWeek);

        //"timestamp" definition is for this hour, allow +/- 2 hours
        for(ScheduleDao schedule : schedules){

                if(fuzzyDoesTimestampMatch(2, date, schedule)) {

                    //is there an active delay for this valve?
                    if(isDelayed(schedule.getValveNumber())){
                        //user notification and logging occurs within the delay check
                        continue;
                    }

                    //has watering already executed for this time?
                    RunHistoryDao runHistory = runHistoryRepository.findByDayRunAndHourRunAndValveNumber(schedule.getDayOfWeek(), schedule.getHourOfDay(), schedule.getValveNumber());

                    if(runHistory != null){
                        //don't run
                        continue;
                    }else{

                        try {
                            //execute
                            xbeeCommander.sendCommand(WateringRequest.fromSchedule(schedule, "0"));
                        }catch (Exception e){
                            logger.error("Error executing http command to xbee commander: {}", e);
                            //TODO: post to SQS instead with an expiration timer
                        }


                        //save new run history
                        RunHistoryDao newRunHistory = new RunHistoryDao();
                        newRunHistory.setDayRun(schedule.getDayOfWeek());
                        newRunHistory.setExecutionTime(DateTime.now().toDate());
                        newRunHistory.setHourRun(schedule.getHourOfDay());
                        newRunHistory.setRunReason("Timer executed automatically");
                        newRunHistory.setRunTimeMs(schedule.getRunTimeMs());
                        newRunHistory.setValveNumber(schedule.getValveNumber());
                        runHistoryRepository.save(newRunHistory);
                    }
                }

        }
    }

    private boolean isDelayed(int valveNumber){
        //get any delays for this valve number
        DelayDao delay = delayWateringRepository.findOneByValveNumberAndDelayEndTimestampGreaterThan(valveNumber, DateTime.now().toDate());

        boolean isDelayed = (delay != null);

        if(isDelayed){
            if(!delay.isUserNotified()){
                logger.info("Watering will be delayed on valve {} until {}", valveNumber, delay.getDelayEndTimestamp());
                //TODO: notify user
                delay.setUserNotified(true);
                delay.setUserNotifiedTimestamp(DateTime.now().toDate());
                delayWateringRepository.save(delay);
            }
        }

        return isDelayed;
    }


}


