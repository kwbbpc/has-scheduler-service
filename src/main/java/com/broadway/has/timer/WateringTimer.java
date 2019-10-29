package com.broadway.has.timer;

import com.broadway.has.repositories.WateringScheduleRepository;
import com.broadway.has.commander.Commander;
import com.broadway.has.requests.WateringRequest;
import com.broadway.has.repositories.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    //@Value("{$watering.timer.timeWindowInHours")
    private Integer hoursFuzzyAllowance = 2;

    public static boolean fuzzyDoesTimestampMatch(int hoursFuzzinessAllowance, DateTime current, ScheduleDao scheduled){

        logger.info("Comparing times current: {} vs scheduled: {}", current.toString(), scheduled.toString());

        int dayOfWeek = current.getDayOfWeek();

        if(scheduled.getDayOfWeek() == current.getDayOfWeek()) {

            int currentHourOfDay = current.getHourOfDay();
            if(current.getHourOfDay() < hoursFuzzinessAllowance){
                currentHourOfDay += 24;
            }

            int minStartHour = scheduled.getHourOfDay();
            int maxStartHour = scheduled.getHourOfDay() + hoursFuzzinessAllowance;

            if(minStartHour <= currentHourOfDay && currentHourOfDay <= maxStartHour) {
                return true;
            }
        }

        return false;


    }


    @Scheduled(fixedRate = 60000)
    public void checkForWatering(){

        //get the current day & time
        DateTime date = DateTime.now().withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault())));

        //pull schedule out of database for all schedules today
        List<ScheduleDao> schedules = wateringScheduleRepository.findAllByDayOfWeek(date.getDayOfWeek());

        //"timestamp" definition is for this hour, allow +/- 2 hours
        for(ScheduleDao schedule : schedules){

                if(fuzzyDoesTimestampMatch(this.hoursFuzzyAllowance, date, schedule)) {

                    //is there an active delay for this valve?
                    if(isDelayed(schedule.getValveNumber())){
                        //user notification and logging occurs within the delay check
                        continue;
                    }

                    //will there be a valve already running (or still running)?
                    RunHistoryDao runAlreadyScheduled = runHistoryRepository.findByValveNumberAndProjectedCompletionTimeAfter(schedule.getValveNumber(), DateTime.now().toDate());
                    if(runAlreadyScheduled != null){
                        logger.warn("Multiple runs are scheduled for {} time frame! see conflicting scheduled runs: {}, {}", runAlreadyScheduled.getActualExecutionTime(),
                                runAlreadyScheduled.getScheduleId(), schedule.getId());
                        logger.warn("Run scheduled: {}, Conflicting Schedule: {}", runAlreadyScheduled.toString(), schedule.toString());
                        continue;
                    }

                    //was a valve run within the last X hours?
                    DateTime startWindow = DateTime.now().minusHours(hoursFuzzyAllowance);
                    DateTime endWindow = DateTime.now().plusHours(hoursFuzzyAllowance);
                    RunHistoryDao runRecently =
                            runHistoryRepository.findOneByValveNumberAndActualExecutionTimeBetween(
                                    schedule.getValveNumber(), startWindow.toDate(), endWindow.toDate());
                    if(runRecently != null){
                        logger.warn("A run was recently executed for valve {} at {} which is within the last {} hours of " +
                                        "the configured time window for requested time to run of {}: {}",
                                runRecently.getValveNumber(), runRecently.getActualExecutionTime(), hoursFuzzyAllowance,
                                DateTime.now(), runRecently.toString());
                        logger.warn("This run will be skipped for valve {} at requested schedule {}@{}:{} day {}",
                                schedule.getValveNumber(), schedule.getId(), schedule.getHourOfDay(), schedule.getMinuteOfDay(),
                                schedule.getDayOfWeek());
                        continue;
                    }


                    //has watering already executed for this time?
                    //check the run history to see if it was this actual schedule record that was run, otherwise allow it
                    RunHistoryDao runHistory = runHistoryRepository.findByScheduleIdAndExecutionMidnightDateId(schedule.getId(), DateTime.now().toDateTimeISO().toDateMidnight().toDate());


                    if(runHistory != null){
                        //don't run
                        continue;
                    }else{

                        try {
                            //execute
                            xbeeCommander.sendCommand(WateringRequest.fromSchedule(schedule, "0"));
                        }catch (Exception e){
                            logger.error("Error executing http command to xbee commander: {}", e);
                            return;
                        }


                        //save new run history
                        RunHistoryDao newRunHistory = new RunHistoryDao();
                        newRunHistory.setDayRun(schedule.getDayOfWeek());
                        newRunHistory.setActualExecutionTime(DateTime.now().toDate());
                        newRunHistory.setHourRun(schedule.getHourOfDay());
                        newRunHistory.setRunReason("Timer executed automatically");
                        newRunHistory.setRunTimeMs(schedule.getRunTimeMs());
                        newRunHistory.setValveNumber(schedule.getValveNumber());
                        newRunHistory.setScheduleId(schedule.getId());
                        newRunHistory.setExecutionMidnightDateId(DateTime.now().toDateTimeISO().toDateMidnight().toDate());
                        newRunHistory.setProjectedCompletionTime(DateTime.now().plus(schedule.getRunTimeMs()).toDate());
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


