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
import org.jetbrains.annotations.Async;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.List;

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


        MockitoAnnotations.initMocks(this);

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

        Mockito.verify(commander, Mockito.times(1)).sendCommand(Mockito.any(WateringRequest.class));



    }

    @Test
    public void TestDeleteSchedule(){



    }

    @Test
    public void TestExecutionOn24HourBoundary(){



    }

    @Test
    public void TestMultipleValveSchedules(){



    }
}
