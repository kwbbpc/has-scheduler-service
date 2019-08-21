package com.broadway.has.core.requests;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class WaterSchedule {



    private int valveNumber;
    private List<DaySchedule> schedule;


    public WaterSchedule(){

    }

    public List<DaySchedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<DaySchedule> schedule) {
        this.schedule = schedule;
    }

    public int getValveNumber() {
        return valveNumber;
    }

    public void setValveNumber(int valveNumber) {
        this.valveNumber = valveNumber;
    }


}
