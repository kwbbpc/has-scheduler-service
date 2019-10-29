package com.broadway.has.integration;


import com.broadway.has.Application;
import com.broadway.has.commander.Commander;
import com.broadway.has.integration.common.TestUtils;
import com.broadway.has.repositories.ScheduleDao;
import com.broadway.has.responses.WateringScheduleResponse;
import com.broadway.has.scheduler.Scheduler;
import com.broadway.has.scheduler.WaterSchedule;
import com.broadway.has.timer.WateringTimer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration(initializers = {ITTestDeleteSchedule.Initializer.class})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class})
@ActiveProfiles(value = "test")
public class ITTestDeleteSchedule {



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
    public void TestDeleteSchedule(){


        WaterSchedule testSchedule = TestUtils.buildTestSchedule();
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
}
