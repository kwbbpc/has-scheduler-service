package com.broadway.has.repositories;


import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
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
    private String timeZone;

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

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return "ScheduleDao{" +
                "id='" + id + '\'' +
                ", valveNumber=" + valveNumber +
                ", runTimeMs=" + runTimeMs +
                ", dayOfWeek=" + dayOfWeek +
                ", hourOfDay=" + hourOfDay +
                ", minuteOfDay=" + minuteOfDay +
                ", timeZone='" + timeZone + '\'' +
                '}';
    }
}
