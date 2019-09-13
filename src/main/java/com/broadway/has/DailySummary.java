package com.broadway.has;

import org.springframework.scheduling.annotation.Scheduled;

public class DailySummary {

    @Scheduled(cron = "")
    public void sendDailySchedule(){
        //grab the scheduled watering information for today

        //combine any active delays to decide whether or not we're watering today

        //send the projected summary
    }
}
