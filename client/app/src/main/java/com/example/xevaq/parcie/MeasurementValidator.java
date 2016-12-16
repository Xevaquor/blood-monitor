package com.example.xevaq.parcie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xevaq on 13-Dec-16.
 */

public class MeasurementValidator {
    public Boolean isValid(
            String pulse,
            String systolic,
            String diastolic,
            String date,
            String time
    ) {
        try {
            int npulse = Integer.parseInt(pulse);
            int nsystolic = Integer.parseInt(systolic);
            int ndiastolic = Integer.parseInt(diastolic);
            String datestring = date + " " + time;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm");
            dateFormat.parse(datestring);


            Boolean valid =
                    npulse > 30 && npulse < 300 &&
                            nsystolic > 30 && npulse < 300
                    && ndiastolic > 30 && ndiastolic < 300;
            return valid;


        } catch (Exception e) {
            return false;
        }

    }
}
