package com.broadway.has;

import com.broadway.has.repositories.DelayDao;
import com.broadway.has.repositories.DelayWateringRepository;
import com.broadway.has.repositories.WateringScheduleRepository;
import com.broadway.has.requests.DelayRequest;
import com.broadway.has.scheduler.WaterSchedule;
import com.broadway.has.scheduler.Scheduler;
import com.broadway.has.httpexceptions.InvalidJsonResponseError;
import com.broadway.has.responses.WateringScheduleResponse;
import io.micrometer.core.instrument.Counter;
import io.swagger.annotations.ApiParam;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Controller
public class HasSchedulerController {


    private static Logger logger = LoggerFactory.getLogger(HasSchedulerController.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private WateringScheduleRepository wateringScheduleRepository;

    @Autowired
    private DelayWateringRepository delayWateringRepository;

    @Value("$spring.application.name}")
    String appName;


    @GetMapping(value = "/watering", produces = "application/json")
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
     * Takes a SMTWTFS array with times to regularly execute watering.  Hours MUST be relative to system default (i.e. EST)
     * @return
     */
    @PostMapping(value = "/watering", produces = "application/json")
    public ResponseEntity scheduleWateringTask(
            @ApiParam(value = "Watering schedule to be executed", required = true) @Valid @RequestBody WaterSchedule schedule){

        Counter wateringEndpointCounter = Application.meterRegistry.counter("watering");

        wateringEndpointCounter.increment();

        //update the db settings with the days to water and the times
        scheduler.saveNewSchedule(schedule);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(value = "/watering", produces = "application/json")
    public ResponseEntity scheduleWateringTask(
            @ApiParam(value = "Watering schedule to be executed", required = true) @Valid @RequestBody String id){

        Counter wateringEndpointCounter = Application.meterRegistry.counter("watering");

        wateringEndpointCounter.increment();

        //update the db settings with the days to water and the times
        scheduler.deleteWatering(id);

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Given a delay period (start - stop) save it to the db for delaying any watering
     */
    @PostMapping(value = "/watering/delay", produces = "application/json")
    public ResponseEntity delayWateringTask(@ApiParam(value = "Delay specified scheduled watering until a provided date", required = true) @Valid @RequestBody DelayRequest delayRequest){

        scheduler.delayWatering(delayRequest);

        return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping(value = "/watering/delay", produces = "application/json")
    public ResponseEntity delayWateringTask(@ApiParam(value = "Delay specified scheduled watering until a provided date", required = true) @Valid @RequestBody List<Integer> valveNumbers){

        Map<Integer, List<DelayDao>> delays = scheduler.getDelays(DateTime.now().toDate(), valveNumbers);

        return new ResponseEntity(HttpStatus.OK);

    }

    /**
     * Given a delay period (start - stop) save it to the db for delaying any watering
     */
    @DeleteMapping(value = "/watering/delay", produces = "application/json")
    public ResponseEntity delayWateringTask(@ApiParam(value = "Delay specified scheduled watering until a provided date", required = true) @Valid @RequestBody String id){

        scheduler.deleteDelay(id);

        return new ResponseEntity(HttpStatus.OK);

    }

}