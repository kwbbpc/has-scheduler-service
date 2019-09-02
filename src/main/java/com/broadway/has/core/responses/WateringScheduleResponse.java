package com.broadway.has.core.responses;

import com.broadway.has.core.repositories.ScheduleDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WateringScheduleResponse {

    private Map<Integer, List<ScheduleDao>> scheduleByDay;

    public WateringScheduleResponse() {
        this.scheduleByDay = new HashMap<>();
    }

    public void addScheduleForDay(int dayOfWeek, List<ScheduleDao> schedule){
        this.scheduleByDay.put(dayOfWeek, schedule);
    }

    public Map<Integer,  List<ScheduleDao>> getScheduleByDay() {
        return scheduleByDay;
    }

    public void setScheduleByDay(Map<Integer,  List<ScheduleDao>> scheduleByDay) {
        this.scheduleByDay = scheduleByDay;
    }

    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(this);
    }
}
