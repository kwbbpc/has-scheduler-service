package com.broadway.has.responses;

import com.broadway.has.repositories.ScheduleDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WateringScheduleResponse {

    private Map<Integer, List<ScheduleDao>> scheduleByDay;

    public WateringScheduleResponse() {
        this.scheduleByDay = new HashMap<>();
    }

    public void addScheduleForDay(int dayOfWeek, List<ScheduleDao> schedule){
        this.scheduleByDay.put(dayOfWeek, schedule);
    }

    public List<ScheduleDao> getAllSchedules(){
        return scheduleByDay.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public List<ScheduleDao> getScheduleByDay(int day) {
        return scheduleByDay.get(day);
    }

    public void setScheduleByDay(Map<Integer,  List<ScheduleDao>> scheduleByDay) {
        this.scheduleByDay = scheduleByDay;
    }

    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(this);
    }
}
