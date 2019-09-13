package com.broadway.has.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RunHistoryRepository extends
        MongoRepository<RunHistoryDao, String> {

    RunHistoryDao findByDayRunAndHourRunAndValveNumber(int dayRun, int hourRun, int valveNumber);
}
