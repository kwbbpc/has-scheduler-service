package com.broadway.has.repositories;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "wateringRunHistory")
public class RunHistoryDao {

    private String scheduleId;
    private Date executionMidnightDateId;
    private int dayRun;
    private int hourRun;
    private int valveNumber;
    private int runTimeMs;
    private String runReason;
    private Date actualExecutionTime;
    private Date projectedCompletionTime;


    @Id
    private String id;

    public Date getProjectedCompletionTime() {
        return projectedCompletionTime;
    }

    public void setProjectedCompletionTime(Date projectedCompletionTime) {
        this.projectedCompletionTime = projectedCompletionTime;
    }

    public Date getExecutionMidnightDateId() {
        return executionMidnightDateId;
    }

    public void setExecutionMidnightDateId(Date executionMidnightDateId) {
        this.executionMidnightDateId = executionMidnightDateId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Date getActualExecutionTime() {
        return actualExecutionTime;
    }

    public void setActualExecutionTime(Date actualExecutionTime) {
        this.actualExecutionTime = actualExecutionTime;
    }
}
