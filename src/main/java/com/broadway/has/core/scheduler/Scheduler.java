package com.broadway.has.core.scheduler;

import com.amazonaws.services.glue.model.Database;
import com.broadway.has.core.httpexceptions.DatabaseError;
import com.broadway.has.core.repositories.DelayDao;
import com.broadway.has.core.repositories.DelayWateringRepository;
import com.broadway.has.core.repositories.ScheduleDao;
import com.broadway.has.core.repositories.WateringScheduleRepository;
import com.broadway.has.core.requests.DelayRequest;
import com.broadway.has.core.requests.WaterSchedule;
import com.broadway.has.core.responses.WateringScheduleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    WateringScheduleRepository wateringScheduleRepository;

    @Autowired
    DelayWateringRepository delayWateringRepository;

    public void delayWatering(DelayRequest delayRequest){

        try {
            delayWateringRepository.save(DelayRequestConverter.convert(delayRequest, DelayRequestConverter.USER_REQUEST));
        }catch (Exception e){

            logger.error("Error saving watering delay: {}", e);
            throw new DatabaseError();
        }


    }

    public void deleteDelay(String id){
        try{
            delayWateringRepository.deleteById(id);
        }catch (Exception e){
            logger.error("Error deleting delay: {}", e);
            throw new DatabaseError();
        }
    }

    public Map<Integer, List<DelayDao>> getDelays(Date fromThisTimeOn, List<Integer> valves){

        Map<Integer, List<DelayDao>> delayMap = new HashMap<>();

        for(Integer valve : valves){
            List<DelayDao> delays = delayWateringRepository.findAllByValveNumberAndDelayEndTimestampGreaterThan(valve, fromThisTimeOn);
            if(delays != null && !delays.isEmpty()){
                delayMap.put(valve, delays);
            }
        }

        return delayMap;

    }

    public void saveNewSchedule(WaterSchedule waterScheduleRequest) throws DatabaseError{


        try {
            List<ScheduleDao> schedule = ScheduleRequestConverter.convert(waterScheduleRequest);

            for (ScheduleDao dao : schedule) {
                wateringScheduleRepository.insert(dao);
            }
        }catch (Exception e){
            throw new DatabaseError();
        }


    }

    public void deleteWatering(String id){
        try{
            wateringScheduleRepository.deleteById(id);
        }catch (Exception e){
            logger.error("Error deleting watering: {}", e);
            throw new DatabaseError();
        }
    }

    public WateringScheduleResponse getSchedule(){

        WateringScheduleResponse response = new WateringScheduleResponse();

        for(int i=0; i<=7; ++i){

            List<ScheduleDao> schedules = wateringScheduleRepository.findAllByDayOfWeek(i);
            if(schedules != null && !schedules.isEmpty()){
                response.addScheduleForDay(i, schedules);
            }

        }

        return response;

    }

}
