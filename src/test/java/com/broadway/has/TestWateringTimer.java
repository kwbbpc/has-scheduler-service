package com.broadway.has;

import com.broadway.has.commander.Commander;
import com.broadway.has.requests.WateringRequest;
import com.broadway.has.repositories.*;
import com.broadway.has.timer.WateringTimer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class TestWateringTimer {

    @Mock
    WateringScheduleRepository wateringScheduleRepository;

    @Mock
    RunHistoryRepository runHistoryRepository;

    @Mock
    DelayWateringRepository delayWateringRepository;

    @Mock
    Commander xbeeCommander;


    @InjectMocks
    WateringTimer timer;



    @After
    public void cleanup() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void TestWateringExecutionWhenExpected() throws Exception{

        DateTime testTime = new DateTime(2019, 1,6,13,13,13,13, DateTimeZone.forOffsetHours(-5));
        testTime.getMillis();
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        List<ScheduleDao> list = new ArrayList<>();
        ScheduleDao dao = new ScheduleDao();
        dao.setDayOfWeek(testTime.getDayOfWeek());
        dao.setHourOfDay(testTime.getHourOfDay());
        int c = testTime.getHourOfDay();
        list.add(dao);

        Mockito.when(wateringScheduleRepository.findAllByDayOfWeek(testTime.getDayOfWeek())).thenReturn(list);

        timer.checkForWatering();

        WateringRequest requestToCheck = WateringRequest.fromSchedule(dao, "0");

        //make sure watering was executed according to schedule
        Mockito.verify(xbeeCommander, Mockito.times(1)).sendCommand(requestToCheck);

    }

    @Test
    public void TestWateringNotExecutedOutsideOfTargetTime() throws Exception{

        DateTime testTime = new DateTime(2019, 1,6,13,13,13,13, DateTimeZone.forOffsetHours(-5));
        testTime.getMillis();
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        List<ScheduleDao> list = new ArrayList<>();
        ScheduleDao dao = new ScheduleDao();
        dao.setDayOfWeek(testTime.getDayOfWeek());
        dao.setHourOfDay(testTime.getHourOfDay() + 6);
        list.add(dao);

        Mockito.when(wateringScheduleRepository.findAllByDayOfWeek(testTime.getDayOfWeek())).thenReturn(list);

        timer.checkForWatering();

        WateringRequest requestToCheck = WateringRequest.fromSchedule(dao, "0");

        //make sure watering was executed according to schedule
        Mockito.verify(xbeeCommander, Mockito.times(0)).sendCommand(requestToCheck);

    }

    @Test
    public void TestWateringExecutionOverFuzzyTime() throws Exception{

        DateTime testTime = new DateTime(2019, 1,6,13,13,13,13, DateTimeZone.forOffsetHours(-5));
        testTime.getMillis();
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        List<ScheduleDao> list = new ArrayList<>();
        ScheduleDao dao = new ScheduleDao();
        dao.setDayOfWeek(testTime.getDayOfWeek());
        dao.setHourOfDay(testTime.getHourOfDay() - 1);
        list.add(dao);



        Mockito.when(wateringScheduleRepository.findAllByDayOfWeek(testTime.getDayOfWeek())).thenReturn(list);

        timer.checkForWatering();

        WateringRequest requestToCheck = WateringRequest.fromSchedule(dao, "0");

        //make sure watering was executed according to schedule
        Mockito.verify(xbeeCommander, Mockito.times(1)).sendCommand(requestToCheck);

    }

    @Test
    public void CheckForWateringOn24HourBoundaryMark() throws IOException{

        DateTime testTime = new DateTime(2019, 1,6,01,13,13,13, DateTimeZone.forOffsetHours(-5));
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        List<ScheduleDao> list = new ArrayList<>();
        ScheduleDao dao = new ScheduleDao();
        dao.setDayOfWeek(testTime.getDayOfWeek());
        dao.setHourOfDay(23);
        dao.setMinuteOfDay(50);
        list.add(dao);



        Mockito.when(wateringScheduleRepository.findAllByDayOfWeek(testTime.getDayOfWeek())).thenReturn(list);

        timer.checkForWatering();

        WateringRequest requestToCheck = WateringRequest.fromSchedule(dao, "0");

        //make sure watering was executed according to schedule
        Mockito.verify(xbeeCommander, Mockito.times(1)).sendCommand(requestToCheck);

    }

    public static RunHistoryDao testRunHistoryDao(DateTime testTime, int testValveNumber){
        RunHistoryDao runHistoryDao = new RunHistoryDao();
        runHistoryDao.setDayRun(testTime.getHourOfDay());
        runHistoryDao.setActualExecutionTime(testTime.toDate());
        runHistoryDao.setHourRun(testTime.getHourOfDay());
        runHistoryDao.setRunTimeMs(10000000);
        runHistoryDao.setValveNumber(testValveNumber);
        runHistoryDao.setRunReason("test");
        return runHistoryDao;
    }

    @Test
    public void CheckForWateringSkipWhenRunHistoryAlreadyExists() throws IOException {


        int testValveNumber = 1;

        DateTime testTime = new DateTime(2019, 1,6,13,13,13,13, DateTimeZone.forOffsetHours(-5));
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        List<ScheduleDao> list = new ArrayList<>();
        ScheduleDao dao = new ScheduleDao();
        dao.setDayOfWeek(testTime.getDayOfWeek());
        dao.setHourOfDay(testTime.getHourOfDay());
        dao.setValveNumber(testValveNumber);
        list.add(dao);

        RunHistoryDao runHistoryDao = new RunHistoryDao();
        runHistoryDao.setDayRun(testTime.getHourOfDay());
        runHistoryDao.setActualExecutionTime(testTime.toDate());
        runHistoryDao.setHourRun(testTime.getHourOfDay());
        runHistoryDao.setRunTimeMs(10000000);
        runHistoryDao.setValveNumber(testValveNumber);
        runHistoryDao.setRunReason("test");

        Mockito.when(
                runHistoryRepository.findByDayRunAndHourRunAndValveNumber(
                        testTime.getDayOfWeek(), testTime.getHourOfDay(), testValveNumber))
                .thenReturn(runHistoryDao);
        Mockito.when(wateringScheduleRepository.findAllByDayOfWeek(testTime.getDayOfWeek())).thenReturn(list);

        timer.checkForWatering();

        WateringRequest requestToCheck = WateringRequest.fromSchedule(dao, "0");

        //make sure watering was skipped because there's already a run history at this time.
        Mockito.verify(xbeeCommander, Mockito.times(0)).sendCommand(requestToCheck);


    }

    @Test
    public void VerifyWateringHistoryGetsCreatedOnFirstRun(){


        int testValveNumber = 1;

        DateTime testTime = new DateTime(2019, 1,6,13,13,13,13, DateTimeZone.forOffsetHours(-5));
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        List<ScheduleDao> list = new ArrayList<>();
        ScheduleDao dao = new ScheduleDao();
        dao.setDayOfWeek(testTime.getDayOfWeek());
        dao.setHourOfDay(testTime.getHourOfDay());
        dao.setValveNumber(testValveNumber);
        list.add(dao);


        Mockito.when(
                runHistoryRepository.findByDayRunAndHourRunAndValveNumber(
                        testTime.getDayOfWeek(), testTime.getHourOfDay(), testValveNumber))
                .thenReturn(null);
        Mockito.when(wateringScheduleRepository.findAllByDayOfWeek(testTime.getDayOfWeek())).thenReturn(list);


        //Run the test
        timer.checkForWatering();

        //Verify there was a new history created since this was our first run.
        Mockito.verify(runHistoryRepository, Mockito.times(1)).save(Mockito.any(RunHistoryDao.class));


    }

    @Test
    public void VerifyWateringExecutionHappensOnlyOnce(){
        int testValveNumber = 1;

        DateTime testTime = new DateTime(2019, 1,6,13,13,13,13, DateTimeZone.forOffsetHours(-5));
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        List<ScheduleDao> list = new ArrayList<>();
        ScheduleDao dao = new ScheduleDao();
        dao.setDayOfWeek(testTime.getDayOfWeek());
        dao.setHourOfDay(testTime.getHourOfDay());
        dao.setValveNumber(testValveNumber);
        list.add(dao);


        Mockito.when(
                runHistoryRepository.findByDayRunAndHourRunAndValveNumber(
                        testTime.getDayOfWeek(), testTime.getHourOfDay(), testValveNumber))
                .thenReturn(null);
        Mockito.when(wateringScheduleRepository.findAllByDayOfWeek(testTime.getDayOfWeek())).thenReturn(list);


        //Run the test
        timer.checkForWatering();

        //watering has run, so now we need to return a valid history
        Mockito.when(
                runHistoryRepository.findByDayRunAndHourRunAndValveNumber(
                        testTime.getDayOfWeek(), testTime.getHourOfDay(), testValveNumber))
                .thenReturn(testRunHistoryDao(testTime, testValveNumber));

        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis() + 1);
        timer.checkForWatering();
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis() + 2);
        timer.checkForWatering();

        //Verify there was a new history created since this was our first run.
        Mockito.verify(runHistoryRepository, Mockito.times(1)).save(Mockito.any(RunHistoryDao.class));
    }
}
