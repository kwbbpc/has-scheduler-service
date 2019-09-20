package com.broadway.has;

import com.broadway.has.repositories.DelayWateringRepository;
import com.broadway.has.repositories.ScheduleDao;
import com.broadway.has.repositories.WateringScheduleRepository;
import com.broadway.has.requests.ScheduleRequestConverter;
import com.broadway.has.scheduler.DaySchedule;
import com.broadway.has.scheduler.Scheduler;
import com.broadway.has.scheduler.WaterSchedule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

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

    @Before
    public void init(){
        MockitoAnnotations.initMocks(wateringRepo);
    }



    @Test
    public void TestSaveWateringRequest() throws Exception{

        WaterSchedule schedule = new WaterSchedule();
        DaySchedule day1 = new DaySchedule();
        day1.setDay(1);
        day1.setHours(23);
        day1.setMinutes(30);
        day1.setRunTimeMs(10000);

        DaySchedule day5 = new DaySchedule();
        day5.setDay(5);
        day5.setHours(15);
        day5.setMinutes(55);
        day5.setRunTimeMs(100000);

        schedule.setSchedule(new ArrayList<DaySchedule>(){{
            add(day1);
            add(day5);
        }});

        scheduler.saveNewSchedule(schedule);

        List<ScheduleDao> scheduleDaoToVerify = ScheduleRequestConverter.convert(schedule);

        for(ScheduleDao dao : scheduleDaoToVerify) {
            Mockito.verify(wateringRepo, Mockito.times(2)).insert(Mockito.any(ScheduleDao.class));
        }

    }
}
