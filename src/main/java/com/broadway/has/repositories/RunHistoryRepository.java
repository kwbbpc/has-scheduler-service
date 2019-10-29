package com.broadway.has.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

public interface RunHistoryRepository extends
        MongoRepository<RunHistoryDao, String> {

    RunHistoryDao findByDayRunAndHourRunAndValveNumber(int dayRun, int hourRun, int valveNumber);
    RunHistoryDao findByScheduleIdAndExecutionMidnightDateId(String scheduleId, Date executionDateId);
    RunHistoryDao findByValveNumberAndProjectedCompletionTimeAfter(int valveNumber, Date requestedStartTime);
    RunHistoryDao findOneByValveNumberAndActualExecutionTimeBetween(int valveNumber, Date start, Date end);
}
