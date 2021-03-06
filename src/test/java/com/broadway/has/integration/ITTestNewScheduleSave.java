package com.broadway.has.integration;

import com.broadway.has.Application;
import com.broadway.has.commander.Commander;
import com.broadway.has.integration.common.TestUtils;
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
import org.junit.*;
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
import org.springframework.test.annotation.DirtiesContext;
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
    public static GenericContainer mongoContainer = new GenericContainer("mongo:4.0").withExposedPorts(27017);


    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void cleanup() throws Exception{
    }


    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
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





    @Test
    public void TestNewScheduleSave(){

        WaterSchedule testSchedule = TestUtils.buildTestSchedule();

        scheduler.saveNewSchedule(testSchedule);

        WateringScheduleResponse r = scheduler.getSchedule();

        Assert.assertTrue("Fetched schedules don't match the ones that were saved", CompareSchedules(r, testSchedule));


    }









}
