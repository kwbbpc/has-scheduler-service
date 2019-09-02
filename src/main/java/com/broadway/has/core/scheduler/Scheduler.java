package com.broadway.has.core.scheduler;

import com.amazonaws.services.glue.model.Database;
import com.broadway.has.core.httpexceptions.DatabaseError;
import com.broadway.has.core.repositories.ScheduleDao;
import com.broadway.has.core.repositories.WateringScheduleRepository;
import com.broadway.has.core.requests.WaterSchedule;
import com.broadway.has.core.responses.WateringScheduleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Scheduler {

    @Autowired
    WateringScheduleRepository wateringScheduleRepository;

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
