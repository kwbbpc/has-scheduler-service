package com.broadway.has;

import com.broadway.has.commander.Commander;
import com.broadway.has.commander.WateringRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestExample {

    @Autowired
   private Commander xbeeCommander;


    @Test
    public void TestSave() throws Exception{

        WateringRequest r = new WateringRequest();
        r.setOn(true);
        r.setValveNumber(1);
        r.setXbeeAddr("0");
        r.setRunTimeMs(9999);

       // xbeeCommander.sendCommand(r);
    }
}
