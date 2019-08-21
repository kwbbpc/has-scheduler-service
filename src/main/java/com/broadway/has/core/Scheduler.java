package com.broadway.has.core;

import org.springframework.scheduling.annotation.Scheduled;

public class Scheduler {


    @Scheduled(fixedDelay = 60000)
    public void checkForWatering(){

        // check db for scheduled timing information


        // are we under an active delay?
            //exit early

        // get last watered timestamp

        // compare timestamps and if watering has executed for this time yet


        // if watering hasn't executed yet, fire new task to water

            //update last watered timestamp

        return;

    }

}
