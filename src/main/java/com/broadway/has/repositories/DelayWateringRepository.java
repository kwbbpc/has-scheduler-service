package com.broadway.has.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface DelayWateringRepository extends
        MongoRepository<DelayDao, String> {

    DelayDao findOneByValveNumberAndDelayEndTimestampGreaterThan(int valveNumber, Date currentTime);

    List<DelayDao> findAllByValveNumberAndDelayEndTimestampGreaterThan(int valveNumber, Date currentTime);

}
