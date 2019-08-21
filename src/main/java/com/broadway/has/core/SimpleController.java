package com.broadway.has.core;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.broadway.has.core.repositories.WateringScheduleRepository;
import com.broadway.has.core.requests.WaterSchedule;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;




@Controller
public class SimpleController {


    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private WateringScheduleRepository wateringScheduleRepository;


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
    public String scheduleWateringTask(
            @ApiParam(value = "Watering schedule to be executed", required = true) @Valid @RequestBody WaterSchedule schedule){

        //update the db settings with the days to water and the times
        WateringScheduleRepository wateringScheduleRepository;
        //TODO: Convert this to a regular schedule object.
        wateringScheduleRepository.save(schedule);

        return "";
    }

    /**
     * Given a delay period (start - stop) save it to the db for delaying any watering
     */
    @PostMapping("/scheduler/delay")
    public Map<String, String> delayWateringTask(){

        //parse out the request

        //update the db with a delay/skip date
        HashMap<String, String> test = new HashMap<>();
        test.put("This", "that");
        return test;
    }

}