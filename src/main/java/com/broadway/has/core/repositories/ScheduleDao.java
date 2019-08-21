package com.broadway.has.core.repositories;


import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "WateringSchedule")
public class ScheduleDao {

    private int valveNumber;
    private int runTimeMs;
    private int dayOfWeek;
    private int hourOfDay;
    private int minuteOfDay;

    @DynamoDBHashKey
    public int getValveNumber() {
        return valveNumber;
    }

    @DynamoDBRangeKey
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinuteOfDay() {
        return minuteOfDay;
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

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public void setMinuteOfDay(int minuteOfDay) {
        this.minuteOfDay = minuteOfDay;
    }
}
