package com.broadway.has.integration.common;

import com.broadway.has.scheduler.DaySchedule;
import com.broadway.has.scheduler.WaterSchedule;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {


     public static WaterSchedule buildTestSchedule(){

        DaySchedule d1 = new DaySchedule();
        d1.setRunTimeMs(5000);
        d1.setMinutes(40);
        d1.setHours(16);
        d1.setDay(1);


        DaySchedule d2 = new DaySchedule();
        d2.setRunTimeMs(5000);
        d2.setMinutes(40);
        d2.setHours(16);
        d2.setDay(3);


        DaySchedule d3 = new DaySchedule();
        d3.setRunTimeMs(5000);
        d3.setMinutes(40);
        d3.setHours(16);
        d3.setDay(5);

        List<DaySchedule> days = new ArrayList<>();
        days.add(d1);
        days.add(d2);
        days.add(d3);


        WaterSchedule s = new WaterSchedule();
        s.setValveNumber(3);
        s.setSchedule(days);

        return s;
    }
}
