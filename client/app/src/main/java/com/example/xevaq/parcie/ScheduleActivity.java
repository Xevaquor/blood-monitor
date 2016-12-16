package com.example.xevaq.parcie;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    EditText etTimes;
    int times = 1;

    ScheduleClient scheduleClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);


        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();

        etTimes = (EditText) findViewById(R.id.etScheduleTimes);
        etTimes.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            int n = Integer.parseInt(editable.toString());
                            times = n;
                            setVisibility(n);

                        } catch (NumberFormatException e) {
                        }
                    }

                }
        );
        setVisibility(1);
    }

    protected void setVisibility(int n) {
        List<EditText> boxes = new ArrayList<>();
        boxes.add((EditText) findViewById(R.id.editText2));
        boxes.add((EditText) findViewById(R.id.editText3));
        boxes.add((EditText) findViewById(R.id.editText4));
        boxes.add((EditText) findViewById(R.id.editText5));
        boxes.add((EditText) findViewById(R.id.editText6));

        for (int i = 0; i < boxes.size(); i++) {
            EditText e = boxes.get(i);
            e.setVisibility(i < n ? View.VISIBLE : View.GONE);
        }
    }

//    @Override
//    protected void onStop() {
//        // When our activity is stopped ensure we also stop the connection to the service
//        // this stops us leaking our activity into the system *bad*
//        if (scheduleClient != null)
//            scheduleClient.doUnbindService();
//        super.onStop();
//    }

    public void onOkClicked(View view) {
        List<EditText> boxes = new ArrayList<>();
        boxes.add((EditText) findViewById(R.id.editText2));
        boxes.add((EditText) findViewById(R.id.editText3));
        boxes.add((EditText) findViewById(R.id.editText4));
        boxes.add((EditText) findViewById(R.id.editText5));
        boxes.add((EditText) findViewById(R.id.editText6));

        for (int i = 0; i < boxes.size(); i++) {
            EditText e = boxes.get(i);
            if (i < times) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                try {
                    Calendar xd = Calendar.getInstance();
                    xd.setTime(sdf.parse(e.getText().toString()));// all done
                    cal.set(Calendar.HOUR_OF_DAY, xd.get(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, xd.get(Calendar.MINUTE));
                    scheduleClient.setAlarmForNotification(cal);
                    Toast.makeText(this, "Ustawiono!", Toast.LENGTH_SHORT).show();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void onTimeClicked(final View view) {
        final Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                myCalendar.set(Calendar.MINUTE, minute);
                String myFormat = "HH:mm"; // your format
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                ((EditText) view).setText(sdf.format(myCalendar.getTime()));
            }
        };
        new TimePickerDialog(this, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();

    }
}
