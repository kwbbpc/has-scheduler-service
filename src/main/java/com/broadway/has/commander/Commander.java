package com.broadway.has.commander;

import com.broadway.has.requests.WateringRequest;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@FeignClient(name="xbee-commander", url = "${commander.hostname}")
public interface Commander {

    @PostMapping(value="/watering")
    @Headers("Content-Type: application/json")
    void sendCommand(WateringRequest watering) throws IOException;
}
