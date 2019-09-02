package com.broadway.has.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RunHistoryRepository extends
        MongoRepository<RunHistoryDao, String> {

    RunHistoryDao findByDayRunAndHourRunAndValveNumber(int dayRun, int hourRun, int valveNumber);
}
