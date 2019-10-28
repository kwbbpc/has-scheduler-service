package com.broadway.has.integration;

import com.broadway.has.Application;
import com.broadway.has.commander.Commander;
import com.broadway.has.repositories.ScheduleDao;
import com.broadway.has.requests.WateringRequest;
import com.broadway.has.responses.WateringScheduleResponse;
import com.broadway.has.scheduler.DaySchedule;
import com.broadway.has.scheduler.Scheduler;
import com.broadway.has.scheduler.WaterSchedule;
import com.broadway.has.timer.WateringTimer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration(initializers = {ITTestNewScheduleSave.Initializer.class})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class})
@ActiveProfiles(value = "test")
public class ITTestNewScheduleSave {

    @Autowired
    @InjectMocks
    private WateringTimer wateringTimer;

    @Autowired
    private Scheduler scheduler;

    @Mock
    private Commander commander;


    @ClassRule
    public static GenericContainer mongoContainer
            = new GenericContainer("mongo:4.0").withExposedPorts(27017);

    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }


    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            int x = mongoContainer.getMappedPort(27017);
            TestPropertyValues.of(
                    "com.broadway.has.mongo.port=" + mongoContainer.getMappedPort(27017),
                    "com.broadway.has.mongo.host=" + "localhost"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    private static boolean CompareSchedules(DaySchedule daySchedule, ScheduleDao scheduleDao){
        boolean equals = true;
        equals &= daySchedule.getDay() == scheduleDao.getDayOfWeek();
        equals &= daySchedule.getHours() == scheduleDao.getHourOfDay();
        equals &= daySchedule.getMinutes() == scheduleDao.getMinuteOfDay();
        equals &= daySchedule.getRunTimeMs() == scheduleDao.getRunTimeMs();

        return equals;
    }

    private static boolean CompareSchedules(WateringScheduleResponse response, WaterSchedule expectedSchedule){

        List<DaySchedule> expected = expectedSchedule.getSchedule();

        for(DaySchedule expectedSingle : expected){

            List<ScheduleDao> responseSchedules = response.getScheduleByDay(expectedSingle.getDay());

            //find a matching schedule in the list
            boolean match = false;
            for(ScheduleDao singleResponse : responseSchedules){

                if(CompareSchedules(expectedSingle, singleResponse)){
                    match = true;
                    break;
                }

            }

            if(!match){
                return false;
            }

        }

        return true;

    }


    private WaterSchedule buildTestSchedule(){

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

    @Autowired
    private ApplicationContext context;

    @Test
    public void TestNewScheduleSave(){

        WaterSchedule testSchedule = buildTestSchedule();

        scheduler.saveNewSchedule(testSchedule);

        WateringScheduleResponse r = scheduler.getSchedule();

        Assert.assertTrue("Fetched schedules don't match the ones that were saved", CompareSchedules(r, testSchedule));


    }

    @Test
    public void TestSingleScheduleExecution() throws Exception{


        WaterSchedule testSchedule = buildTestSchedule();
        scheduler.saveNewSchedule(testSchedule);

        //fix the current time so the schedule will execute
        DaySchedule test = testSchedule.getSchedule().get(0);
        DateTime testTime = new DateTime(2019, 1,7,test.getHours(),test.getMinutes(),13,13, DateTimeZone.forOffsetHours(-5));
        testTime.getMillis();
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());


        wateringTimer.checkForWatering();
        wateringTimer.checkForWatering();
        wateringTimer.checkForWatering();

        ArgumentCaptor<WateringRequest> capturedXbeeCommanderRequest = ArgumentCaptor.forClass(WateringRequest.class);
        Mockito.verify(commander, Mockito.times(1)).sendCommand(capturedXbeeCommanderRequest.capture());
        WateringRequest actualRequest = capturedXbeeCommanderRequest.getValue();
        Assert.assertTrue("Request was sent for the wrong valve.", actualRequest.getValveNumber() == 3);
        Assert.assertTrue("Request was not specifying 'on' value.", actualRequest.isOn());

    }

    @Test
    public void TestDeleteSchedule(){


        WaterSchedule testSchedule = buildTestSchedule();
        scheduler.saveNewSchedule(testSchedule);

        //verify there's 3 schedules
        WateringScheduleResponse r = scheduler.getSchedule();
        int x = r.getAllSchedules().size();
        Assert.assertTrue("Expected number of schedules was not created.", r.getAllSchedules().size() == 3);
        List<String> ids = r.getAllSchedules().stream().map(ScheduleDao::getId).collect(Collectors.toList());

        //delete a watering
        ScheduleDao s = r.getScheduleByDay(1).get(0);
        scheduler.deleteWatering(s.getId());
        String deletedId = s.getId();

        //verify it was deleted
        WateringScheduleResponse r2 = scheduler.getSchedule();
        Assert.assertTrue("Expected number of schedules was not created.", r2.getAllSchedules().size() == 2);
        List<String> idsAfterDelete = r2.getAllSchedules().stream().map(ScheduleDao::getId).collect(Collectors.toList());
        Assert.assertFalse(idsAfterDelete.contains(deletedId));

    }

    private static void verifyWateringIsNotExecutedWhenItIsntSupposedTo(WateringTimer wateringTimer, Commander commander) throws Exception{
        //set the time to not run
        DateTime testTime = new DateTime(2019, 1, 7, 0, 13, 13, DateTimeZone.forOffsetHours(-5));
        testTime.getMillis();
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        //check watering
        wateringTimer.checkForWatering();
        Mockito.verify(commander, Mockito.times(0)).sendCommand(Mockito.any(WateringRequest.class));
    }

    @Test
    public void TestMultipleValveSchedules() throws Exception{


            WaterSchedule testSchedule = buildTestSchedule();
            scheduler.saveNewSchedule(testSchedule);

        {
            WaterSchedule testSchedule2 = buildTestSchedule();
            testSchedule2.setValveNumber(4);
            scheduler.saveNewSchedule(testSchedule2);
        }
        {
            WaterSchedule testSchedule3 = buildTestSchedule();
            testSchedule3.setValveNumber(5);
            scheduler.saveNewSchedule(testSchedule3);
        }
        {
            //this schedule should be different and therefore NOT execute at the same time as the others.
            WaterSchedule testSchedule4 = buildTestSchedule();
            testSchedule4.setValveNumber(6);
            testSchedule4.getSchedule().get(0).setHours(12);
            scheduler.saveNewSchedule(testSchedule4);
        }
        verifyWateringIsNotExecutedWhenItIsntSupposedTo(wateringTimer, commander);

        //set the time to actually run
        DaySchedule test = testSchedule.getSchedule().get(0);
        DateTime testTime = new DateTime(2019, 1,7,test.getHours(),test.getMinutes(),13,13, DateTimeZone.forOffsetHours(-5));
        testTime.getMillis();
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        //check for watering, now it's supposed to execute for every valve (3,4,5) except 6
        wateringTimer.checkForWatering();
        ArgumentCaptor<WateringRequest> capturedXbeeCommanderRequest = ArgumentCaptor.forClass(WateringRequest.class);
        Mockito.verify(commander, Mockito.times(3)).sendCommand(capturedXbeeCommanderRequest.capture());
        Set<Integer> actualRequest = capturedXbeeCommanderRequest.
                getAllValues().stream().map(WateringRequest::getValveNumber).collect(Collectors.toSet());

        Set<Integer> expectedValvesToRun = new HashSet<>(Arrays.asList(new Integer[]{3,4,5}));
        Assert.assertEquals("Not all valves were called as expected", expectedValvesToRun, actualRequest);

    }


    @Test
    public void TestDuplicateRuns() throws Exception
    {

        //run through the previous test twice, and verify it isn't executing things multiple times
        TestMultipleValveSchedules();

        WaterSchedule testSchedule = buildTestSchedule();
        scheduler.saveNewSchedule(testSchedule);

        {
            WaterSchedule testSchedule2 = buildTestSchedule();
            testSchedule2.setValveNumber(4);
            scheduler.saveNewSchedule(testSchedule2);
        }
        {
            WaterSchedule testSchedule3 = buildTestSchedule();
            testSchedule3.setValveNumber(5);
            scheduler.saveNewSchedule(testSchedule3);
        }

        //set the time to actually run
        DaySchedule test = testSchedule.getSchedule().get(0);
        DateTime testTime = new DateTime(2019, 1,7,test.getHours(),test.getMinutes() + 19,13,13, DateTimeZone.forOffsetHours(-5));
        testTime.getMillis();
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        //check for watering, now it's supposed to execute for every valve (3,4,5) except 6
        wateringTimer.checkForWatering();
        ArgumentCaptor<WateringRequest> capturedXbeeCommanderRequest = ArgumentCaptor.forClass(WateringRequest.class);
        Mockito.verify(commander, Mockito.times(3)).sendCommand(capturedXbeeCommanderRequest.capture());
        Set<Integer> actualRequest = capturedXbeeCommanderRequest.
                getAllValues().stream().map(WateringRequest::getValveNumber).collect(Collectors.toSet());

        Set<Integer> expectedValvesToRun = new HashSet<>(Arrays.asList(new Integer[]{3,4,5}));
        Assert.assertEquals("Not all valves were called as expected", expectedValvesToRun, actualRequest);
    }



    @Test
    public void TestMultiWeekRun() throws Exception
    {

        //run through the previous test twice, and verify it isn't executing things multiple times

        WaterSchedule testSchedule = buildTestSchedule();
        scheduler.saveNewSchedule(testSchedule);

        {
            //set the time to actually run
            DaySchedule test = testSchedule.getSchedule().get(0);
            DateTime testTime = new DateTime(2019, 1, 7, test.getHours(), test.getMinutes(), 13, 13, DateTimeZone.forOffsetHours(-5));
            testTime.getMillis();
            DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());
        }
        //check for watering
        wateringTimer.checkForWatering();
        Mockito.verify(commander, Mockito.times(1)).sendCommand(Mockito.any(WateringRequest.class));


        {
            //advance time
            DaySchedule test = testSchedule.getSchedule().get(0);
            DateTime testTime = new DateTime(2019, 1, 7, test.getHours() + 4, test.getMinutes() + 19, 13, 13, DateTimeZone.forOffsetHours(-5));
            testTime.getMillis();
            DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());
        }
        //check for watering
        wateringTimer.checkForWatering();
        //should still just be 1 time
        Mockito.verify(commander, Mockito.times(1)).sendCommand(Mockito.any(WateringRequest.class));


        {
            //advance time
            DaySchedule test = testSchedule.getSchedule().get(1);
            DateTime testTime = new DateTime(2019, 1, 9, test.getHours(), test.getMinutes(), 13, 13, DateTimeZone.forOffsetHours(-5));
            testTime.getMillis();
            DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());
        }
        //check for watering
        wateringTimer.checkForWatering();
        Mockito.verify(commander, Mockito.times(2)).sendCommand(Mockito.any(WateringRequest.class));


        {
            //advance time
            DaySchedule test = testSchedule.getSchedule().get(2);
            DateTime testTime = new DateTime(2019, 1, 11, test.getHours(), test.getMinutes(), 13, 13, DateTimeZone.forOffsetHours(-5));
            testTime.getMillis();
            DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());
        }
        //check for watering
        wateringTimer.checkForWatering();
        Mockito.verify(commander, Mockito.times(3)).sendCommand(Mockito.any(WateringRequest.class));


        {
            //advance time
            DaySchedule test = testSchedule.getSchedule().get(1);
            DateTime testTime = new DateTime(2019, 1, 14, test.getHours(), test.getMinutes(), 13, 13, DateTimeZone.forOffsetHours(-5));
            testTime.getMillis();
            DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());
        }
        //check for watering
        wateringTimer.checkForWatering();
        Mockito.verify(commander, Mockito.times(4)).sendCommand(Mockito.any(WateringRequest.class));



    }
}
