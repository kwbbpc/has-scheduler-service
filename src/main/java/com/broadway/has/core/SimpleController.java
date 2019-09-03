package com.broadway.has.core;

import com.broadway.has.core.httpexceptions.InvalidJsonResponseError;
import com.broadway.has.core.repositories.DelayWateringRepository;
import com.broadway.has.core.repositories.WateringScheduleRepository;
import com.broadway.has.core.requests.DelayRequest;
import com.broadway.has.core.requests.WaterSchedule;
import com.broadway.has.core.responses.WateringScheduleResponse;
import com.broadway.has.core.scheduler.Scheduler;
import io.micrometer.core.instrument.Counter;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@Controller
public class SimpleController {


    private static Logger logger = LoggerFactory.getLogger(SimpleController.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private WateringScheduleRepository wateringScheduleRepository;

    @Autowired
    private DelayWateringRepository delayWateringRepository;

    @Value("$spring.application.name}")
    String appName;


    @GetMapping(value = "/scheduler/schedule", produces = "application/json")
    @ResponseBody
    public WateringScheduleResponse getScheduledTasks(){
        try {
            String s = scheduler.getSchedule().toJSON();
            return scheduler.getSchedule();
        }catch (Exception e){
            logger.error("Error while getting schedule: {}", e);
            throw new InvalidJsonResponseError();
        }
    }


    /**
     * Takes a SMTWTFS array with times to regularly execute watering
     * @return
     */
    @PostMapping("/scheduler/water")
    public ResponseEntity scheduleWateringTask(
            @ApiParam(value = "Watering schedule to be executed", required = true) @Valid @RequestBody WaterSchedule schedule){

        Counter wateringEndpointCounter = Application.meterRegistry.counter("watering");

        wateringEndpointCounter.increment();

        //update the db settings with the days to water and the times
        scheduler.saveNewSchedule(schedule);

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Given a delay period (start - stop) save it to the db for delaying any watering
     */
    @PostMapping("/scheduler/delay")
    public ResponseEntity delayWateringTask(@ApiParam(value = "Delay specified scheduled watering until a provided date", required = true) @Valid @RequestBody DelayRequest delayRequest){

        scheduler.delayWatering(delayRequest);

        return new ResponseEntity(HttpStatus.OK);

    }

}