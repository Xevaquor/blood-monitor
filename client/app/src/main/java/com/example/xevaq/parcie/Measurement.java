package com.example.xevaq.parcie;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by xevaq on 11-Dec-16.
 */

public class Measurement {
    private int id;
    private int pulse;
    private int systolic;
    private int diastolic;
    private Date date;
    private String onlyDate;
    private String onlyTime;

    public Measurement(int id, int pulse, int systolic, int diastolic, Date date) {
        this.id = id;
        this.pulse = pulse;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOnlyDate() {
        return onlyDate;
    }

    public void setOnlyDate(String onlyDate) {
        this.onlyDate = onlyDate;
    }

    public String getOnlyTime() {
        return onlyTime;
    }

    public void setOnlyTime(String onlyTime) {
        this.onlyTime = onlyTime;
    }
}
