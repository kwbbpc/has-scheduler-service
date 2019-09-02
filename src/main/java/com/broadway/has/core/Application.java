package com.broadway.has.core;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProps.class)
@EnableScheduling
public class Application{

    public static ApplicationContext context;

    public static MeterRegistry meterRegistry;

    public static void main(String[] args){

        context = SpringApplication.run(Application.class, args);


        meterRegistry = new SimpleMeterRegistry();// AtlasMeterRegistry(atlasConfig, Clock.SYSTEM);


    }
}
