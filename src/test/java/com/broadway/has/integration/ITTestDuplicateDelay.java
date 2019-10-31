package com.broadway.has.integration;

import com.broadway.has.Application;
import com.broadway.has.commander.Commander;
import com.broadway.has.integration.common.TestUtils;
import com.broadway.has.requests.WateringRequest;
import com.broadway.has.scheduler.Scheduler;
import com.broadway.has.timer.WateringTimer;
import org.joda.time.DateTime;
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
@ContextConfiguration(initializers = {ITTestDuplicateDelay.Initializer.class})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class})
@ActiveProfiles(value = "test")
public class ITTestDuplicateDelay {


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
    public void TestDuplicateDelaysToASchedule() throws Exception {

        DateTime testTime = new DateTime(2019, 1, 7, 4, 0, 0, 0);
        int testValve = 3;

        //create a new schedule
        scheduler.saveNewSchedule(TestUtils.buildSingleSchedule(3, testTime.getDayOfWeek(),
                testTime.getHourOfDay(), testTime.getMinuteOfHour(), 5000));
        scheduler.saveNewSchedule(TestUtils.buildSingleSchedule(4, testTime.getDayOfWeek(),
                testTime.getHourOfDay(), testTime.getMinuteOfHour(), 5000));

        DateTime timestamp = TestUtils.freezeTime(1, 4, 0);


        TestUtils.delayWatering(timestamp, TestUtils.hoursToMs(1), 3, this.getClass().getName(), scheduler);
        TestUtils.delayWatering(timestamp, TestUtils.hoursToMs(1), 3, this.getClass().getName(), scheduler);


        wateringTimer.checkForWatering();
        {
            ArgumentCaptor<WateringRequest> capturedXbeeCommanderRequest = ArgumentCaptor.forClass(WateringRequest.class);
            Mockito.verify(commander, Mockito.times(1)).sendCommand(capturedXbeeCommanderRequest.capture());
            WateringRequest actualRequest = capturedXbeeCommanderRequest.getValue();
            Assert.assertTrue("Wrong valve was delayed.", actualRequest.getValveNumber() == 4);
        }

        int hourAndHalfMs = 5400000;
        timestamp = TestUtils.advanceTime(timestamp, hourAndHalfMs);

        {
            Mockito.reset(commander);
            wateringTimer.checkForWatering();
            ArgumentCaptor<WateringRequest> capturedXbeeCommanderRequest2 = ArgumentCaptor.forClass(WateringRequest.class);
            Mockito.verify(commander, Mockito.times(1)).sendCommand(capturedXbeeCommanderRequest2.capture());
            WateringRequest actualRequest2 = capturedXbeeCommanderRequest2.getValue();
            Assert.assertTrue("Delay did not properly expire.", actualRequest2.getValveNumber() == 3);
        }



    }

}
