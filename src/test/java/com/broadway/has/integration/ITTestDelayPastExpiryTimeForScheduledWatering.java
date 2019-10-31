package com.broadway.has.integration;

import com.broadway.has.Application;
import com.broadway.has.commander.Commander;
import com.broadway.has.integration.common.TestUtils;
import com.broadway.has.requests.DelayRequest;
import com.broadway.has.requests.WateringRequest;
import com.broadway.has.scheduler.Scheduler;
import com.broadway.has.scheduler.WaterSchedule;
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
@ContextConfiguration(initializers = {ITTestDelayPastExpiryTimeForScheduledWatering.Initializer.class})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class})
@ActiveProfiles(value = "test")
public class ITTestDelayPastExpiryTimeForScheduledWatering {


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
    public void TestDelayPastExpirationTime() throws Exception{

         WaterSchedule s = TestUtils.buildSingleSchedule(3, 1,4,0,5000);
         this.scheduler.saveNewSchedule(s);

         DateTime timestamp = TestUtils.freezeTime(1,4,0);

         //now delay
        DelayRequest delay = TestUtils.delayWatering(timestamp, TestUtils.hoursToMs(4), 3, this.getClass().getName(), scheduler);

        wateringTimer.checkForWatering();
        Mockito.verify(commander, Mockito.times(0)).sendCommand(Mockito.any(WateringRequest.class));

        timestamp = TestUtils.advanceTime(timestamp, TestUtils.hoursToMs(2));
        wateringTimer.checkForWatering();
        Mockito.verify(commander, Mockito.times(0)).sendCommand(Mockito.any(WateringRequest.class));

        timestamp = TestUtils.advanceTime(timestamp, TestUtils.hoursToMs(2));
        wateringTimer.checkForWatering();
        Mockito.verify(commander, Mockito.times(0)).sendCommand(Mockito.any(WateringRequest.class));

        timestamp = TestUtils.advanceTime(timestamp, TestUtils.hoursToMs(2));
        wateringTimer.checkForWatering();
        Mockito.verify(commander, Mockito.times(0)).sendCommand(Mockito.any(WateringRequest.class));


    }

}
