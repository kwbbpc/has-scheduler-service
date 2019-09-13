package com.broadway.has.scheduler;

import com.broadway.has.repositories.ScheduleDao;
import com.broadway.has.requests.DaySchedule;
import com.broadway.has.requests.WaterSchedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRequestConverter {

    public static List<ScheduleDao> convert(WaterSchedule schedule){

        List<ScheduleDao> scheduleDaos = new ArrayList<>();

        for(DaySchedule day : schedule.getSchedule()){

            ScheduleDao scheduleDao = new ScheduleDao();
            scheduleDao.setDayOfWeek(day.getDay());
            scheduleDao.setHourOfDay(day.getHours());
            scheduleDao.setMinuteOfDay(day.getMinutes());
            scheduleDao.setValveNumber(schedule.getValveNumber());
            scheduleDao.setRunTimeMs(day.getRunTimeMs());
            scheduleDaos.add(scheduleDao);

        }

        return scheduleDaos;

    }
}