package com.broadway.has.integration.common;

import com.broadway.has.requests.DelayRequest;
import com.broadway.has.scheduler.DaySchedule;
import com.broadway.has.scheduler.Scheduler;
import com.broadway.has.scheduler.WaterSchedule;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestUtils {


   public static WaterSchedule buildSingleSchedule(int valve, int day, int hour, int minute, int runTimeMs){

      DaySchedule d = new DaySchedule();
      d.setHours(hour);
      d.setDay(day);
      d.setMinutes(minute);
      d.setRunTimeMs(runTimeMs);


      WaterSchedule s = new WaterSchedule();
      s.setValveNumber(valve);
      s.setSchedule(Arrays.asList(d));
      return s;

   }

   public static DateTime freezeTime(int dayOfWeek, int hours, int minutes){

       int dayOfWeekFactored = 7 + (dayOfWeek % 7) - 1;

      //fix the current time so the schedule will execute
      DateTime testTime = new DateTime(2019, 1, dayOfWeekFactored, hours, minutes,
              13,13, DateTimeZone.forOffsetHours(-5));
      testTime.getMillis();
      DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

      return testTime;
   }

   public static DateTime advanceTime(DateTime time, int advanceMs){
      DateTime advancedTime = time.plusMillis(advanceMs);
      DateTimeUtils.setCurrentMillisFixed(advancedTime.getMillis());
      return advancedTime;
   }

   public static DelayRequest delayWatering(DateTime currentTime, int delayTimeMs, int valveToDelay, String reason, Scheduler scheduler){
      DelayRequest delay = new DelayRequest();
      delay.setValveNumber(3);
      DateTime delayTime = currentTime.plusHours(1);
      delay.setDelayUntilTimestamp(delayTime.toDate());
      delay.setReason(reason);

      scheduler.delayWatering(delay);
      return delay;
   }


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
