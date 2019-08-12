package com.broadway.has.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProps.class)
@EnableScheduling
public class Application{

    public static ApplicationContext context;

    public static void main(String[] args){

        context = SpringApplication.run(Application.class, args);


    }
}
