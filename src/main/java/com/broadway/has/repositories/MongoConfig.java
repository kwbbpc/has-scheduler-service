package com.broadway.has.repositories;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
public class MongoConfig extends AbstractMongoConfiguration {


    @Value("${com.broadway.has.mongo.port}")
    private String port;

    @Value("${com.broadway.has.mongo.host}")
    private String host;



    @Override
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(host, Integer.parseInt(port));
    }

    @Override
    protected String getMappingBasePackage() {
        return "com.broadway.has.core";
    }
}

