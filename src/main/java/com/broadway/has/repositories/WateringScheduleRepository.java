package com.broadway.has.repositories;

import com.broadway.has.repositories.ScheduleDao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface WateringScheduleRepository extends
        MongoRepository<ScheduleDao, String> {


    List<ScheduleDao> findAllByDayOfWeek(int dayOfWeek);
}