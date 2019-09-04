package com.broadway.has.core.repositories;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "wateringSchedules")
public class ScheduleDao {

    @Id
    private String id;


    private int valveNumber;
    private int runTimeMs;
    private int dayOfWeek;
    private int hourOfDay;
    private int minuteOfDay;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValveNumber() {
        return valveNumber;
    }

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
