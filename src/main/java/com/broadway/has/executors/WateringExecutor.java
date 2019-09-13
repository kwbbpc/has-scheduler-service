package com.broadway.has.executors;


import com.broadway.has.commander.WateringRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WateringExecutor {


        @Autowired
        protected JmsTemplate defaultJmsTemplate;

        public void sendCommand(WateringRequest watering) throws IOException {
            defaultJmsTemplate.convertAndSend("xbee_commands",
                    watering.toJSON());
        }


}
