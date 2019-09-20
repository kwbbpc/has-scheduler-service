package com.broadway.has.requests;

import com.broadway.has.repositories.ScheduleDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class WateringRequest {


    private int valveNumber;
    private long runTimeMs;
    private boolean on;
    private String xbeeAddr;
    private Date expirationTime;

    public String getXbeeAddr() {
        return xbeeAddr;
    }

    public void setXbeeAddr(String xbeeAddr) {
        this.xbeeAddr = xbeeAddr;
    }

    public int getValveNumber() {
        return valveNumber;
    }

    public void setValveNumber(int valveNumber) {
        this.valveNumber = valveNumber;
    }

    public long getRunTimeMs() {
        return runTimeMs;
    }

    public void setRunTimeMs(long runTimeMs) {
        this.runTimeMs = runTimeMs;
    }

    public boolean isOn() {
        return on;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public static WateringRequest fromJSON(String json)
            throws JsonProcessingException, IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.readValue(json, WateringRequest.class);
    }

    public String toJSON() throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public static WateringRequest fromSchedule(ScheduleDao schedule, String xbeeAddr){
        WateringRequest request = new WateringRequest();
        request.runTimeMs = schedule.getRunTimeMs();
        request.valveNumber = schedule.getValveNumber();
        request.xbeeAddr = xbeeAddr;
        request.on = true;

        return request;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WateringRequest that = (WateringRequest) o;
        boolean eq = valveNumber == that.valveNumber &&
                runTimeMs == that.runTimeMs &&
                on == that.on;

        if(xbeeAddr != null){
            eq &= xbeeAddr.equals((((WateringRequest) o).xbeeAddr));
        }

        if(expirationTime != null){
            eq &= expirationTime.equals(((WateringRequest) o).expirationTime);
        }

        return eq;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valveNumber, runTimeMs, on, xbeeAddr, expirationTime);
    }
}
