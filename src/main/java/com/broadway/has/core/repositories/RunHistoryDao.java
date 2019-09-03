package com.broadway.has.core.repositories;


import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "wateringRunHistory")
public class RunHistoryDao {

    private int dayRun;
    private int hourRun;
    private int valveNumber;
    private int runTimeMs;
    private String runReason;
    private Date executionTime;


    @Id
    private String id;

    public int getDayRun() {
        return dayRun;
    }

    public void setDayRun(int dayRun) {
        this.dayRun = dayRun;
    }

    public int getHourRun() {
        return hourRun;
    }

    public void setHourRun(int hourRun) {
        this.hourRun = hourRun;
    }

    public int getValveNumber() {
        return valveNumber;
    }

    public void setValveNumber(int valveNumber) {
        this.valveNumber = valveNumber;
    }

    public int getRunTimeMs() {
        return runTimeMs;
    }

    public void setRunTimeMs(int runTimeMs) {
        this.runTimeMs = runTimeMs;
    }

    public String getRunReason() {
        return runReason;
    }

    public void setRunReason(String runReason) {
        this.runReason = runReason;
    }

    public Date getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Date executionTime) {
        this.executionTime = executionTime;
    }
}
