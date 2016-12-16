package com.example.xevaq.parcie;

import java.util.Date;

/**
 * Created by xevaq on 11-Dec-16.
 */

public class Measurement {
    private int id;
    private int pulse;
    private int systolic;
    private int diastolic;
    private Date date;

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
}
