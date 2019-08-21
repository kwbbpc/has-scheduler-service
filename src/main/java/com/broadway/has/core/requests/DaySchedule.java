package com.broadway.has.core.requests;

public class DaySchedule {

    private Integer day;
    private Integer hours;
    private Integer minutes;
    private Integer runTimeMs;

    public DaySchedule() {
    }

    public DaySchedule(Integer day, Integer hours, Integer minutes) {
        this.day = day;
        this.hours = hours;
        this.minutes = minutes;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getRunTimeMs() {
        return runTimeMs;
    }

    public void setRunTimeMs(Integer runTimeMs) {
        this.runTimeMs = runTimeMs;
    }
}
