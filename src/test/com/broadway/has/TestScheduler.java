package com.broadway.has;

import com.broadway.has.commander.Commander;
import com.broadway.has.commander.WateringRequest;
import com.broadway.has.mocks.MockCommander;
import com.broadway.has.repositories.DelayWateringRepository;
import com.broadway.has.repositories.WateringScheduleRepository;
import com.broadway.has.scheduler.Scheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class TestScheduler {

    @Mock
    WateringScheduleRepository wateringRepo;


    @Mock
    DelayWateringRepository delayWateringRepository;

    @InjectMocks
    private Scheduler scheduler;


    @Test
    public void TestSave() throws Exception{



        scheduler.deleteDelay("TEST ID");
    }
}
