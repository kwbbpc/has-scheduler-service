package com.broadway.has.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class SimpleController {

    @Value("$spring.application.name}")
    String appName;

    @GetMapping("/")
    public String homePage(Model model){
        model.addAttribute("appName", appName);
        return "home";
    }

    @GetMapping("/test")
    public String testEndpoint(){
        return "it worked";
    }


    @GetMapping("/scheduler")
    public String getScheduledTasks(){
        return "";
    }


    /**
     * Takes a SMTWTFS array with times to regularly execute watering
     * @return
     */
    @PostMapping("/scheduler/water")
    public String scheduleWateringTask(){

        //parse out the request

        //update the db settings with the days to water and the times
        return "";
    }

    /**
     * Given a delay period (start - stop) save it to the db for delaying any watering
     */
    @PostMapping("/scheduler/delay")
    public  String delayWateringTask(){

        //parse out the request

        //update the db with a delay/skip date
        return "";
    }

}