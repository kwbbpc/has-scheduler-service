package com.broadway.has.core.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


@EnableScan
public interface WateringScheduleRepository extends
        CrudRepository<ScheduleDao, String> {

    List<ScheduleDao> findById(String id);
}