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


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration(initializers = {ITTestSingleScheduleExecution.Initializer.class})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class})
@ActiveProfiles(value = "test")
public class ITTestSingleScheduleExecution {



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
        System.out.println("Mongo started on port " + mongoContainer.getMappedPort(27017));
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
    public void TestSingleScheduleExecution() throws Exception{


        WaterSchedule testSchedule = TestUtils.buildTestSchedule();
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
}
