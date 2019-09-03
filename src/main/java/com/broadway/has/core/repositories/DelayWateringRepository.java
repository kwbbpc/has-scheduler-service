package com.broadway.has.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface DelayWateringRepository extends
        MongoRepository<DelayDao, String> {

    DelayDao findByValveNumberAndDelayEndTimestampGreaterThan(int valveNumber, Date currentTime);


}
