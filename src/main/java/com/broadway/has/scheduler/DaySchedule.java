package com.broadway.has.scheduler;

import io.swagger.annotations.ApiModelProperty;

public class DaySchedule {

    @ApiModelProperty(notes = "MONDAY = 1" +
            "\n TUESDAY = 2" +
            "\n WEDNESDAY = 3" +
            "\n THURSDAY = 4" +
            "\n FRIDAY = 5" +
            "\n SATURDAY = 6" +
            "\n SUNDAY = 7")
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
