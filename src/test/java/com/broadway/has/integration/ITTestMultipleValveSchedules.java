package com.broadway.has.integration;

import com.broadway.has.Application;
import com.broadway.has.commander.Commander;
import com.broadway.has.integration.common.TestUtils;
import com.broadway.has.requests.WateringRequest;
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
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration(initializers = {ITTestMultipleValveSchedules.Initializer.class})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class})
@ActiveProfiles(value = "test")
public class ITTestMultipleValveSchedules {


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

    @Test
    public void TestMultipleValveSchedules() throws Exception{


        WaterSchedule testSchedule = TestUtils.buildTestSchedule();
        scheduler.saveNewSchedule(testSchedule);

        {
            WaterSchedule testSchedule2 = TestUtils.buildTestSchedule();
            testSchedule2.setValveNumber(4);
            scheduler.saveNewSchedule(testSchedule2);
        }
        {
            WaterSchedule testSchedule3 = TestUtils.buildTestSchedule();
            testSchedule3.setValveNumber(5);
            scheduler.saveNewSchedule(testSchedule3);
        }
        {
            //this schedule should be different and therefore NOT execute at the same time as the others.
            WaterSchedule testSchedule4 = TestUtils.buildTestSchedule();
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


    private static void verifyWateringIsNotExecutedWhenItIsntSupposedTo(WateringTimer wateringTimer, Commander commander) throws Exception{
        //set the time to not run
        DateTime testTime = new DateTime(2019, 1, 7, 0, 13, 13, DateTimeZone.forOffsetHours(-5));
        testTime.getMillis();
        DateTimeUtils.setCurrentMillisFixed(testTime.getMillis());

        //check watering
        wateringTimer.checkForWatering();
        Mockito.verify(commander, Mockito.times(0)).sendCommand(Mockito.any(WateringRequest.class));
    }

}
