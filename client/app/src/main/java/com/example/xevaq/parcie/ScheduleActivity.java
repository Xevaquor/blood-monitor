package com.example.xevaq.parcie;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ScheduleActivity extends AppCompatActivity {

    EditText etTimes;
    int times = 1;

    ScheduleClient scheduleClient;
    private EditText etUntil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);


        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();

        etTimes = (EditText) findViewById(R.id.etScheduleTimes);
        etUntil = (EditText) findViewById(R.id.etLastAlarm);
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




                    String eventUriString = "content://com.android.calendar/events";
                    ContentValues eventValues = new ContentValues();

                    eventValues.put("calendar_id", 1); // id, We need to choose from
                    // our mobile for primary
                    // its 1
                    eventValues.put("title", "Pomiar ciśnienia");
                    eventValues.put("description", "Pamiętaj aby dokonać pomiaru");

                    Calendar beginTime = Calendar.getInstance();
                    //beginTime.set(2017, 0, 15, 21, 30);
                    beginTime.set(Calendar.HOUR_OF_DAY, xd.get(Calendar.HOUR_OF_DAY));
                    beginTime.set(Calendar.MINUTE, xd.get(Calendar.MINUTE));
                    beginTime.add(Calendar.DATE, 0);
                    long startMillis = beginTime.getTimeInMillis();
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(2017, 0, 18, 8, 45);
                    long endMillis = endTime.getTimeInMillis();
                    endMillis = startMillis + 1000;
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
                    Date end = sdf2.parse(etUntil.getText().toString());
                    Calendar dt = Calendar.getInstance();
                    dt.setTime(end);
//                    dt.add(Calendar.DATE, 1);
//                    dt.add(Calendar.YEAR,1);
//                    dt.add(Calendar.,1);
                    SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
                    String dtUntil = yyyymmdd.format(dt.getTime());

                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    intent.setType("vnd.android.cursor.item/event");
//                    intent.putExtra("beginTime", beginTime);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            beginTime.getTimeInMillis());
//                    intent.putExtra("allDay", true);
                    intent.putExtra("rrule", "FREQ=DAILY;UNTIL="+dtUntil);
//                    intent.putExtra("endTime", end);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startMillis + 1000*60*30);
                    intent.putExtra("title", "Zmierz ciśnienie!");
//                    intent.putExtra(CalendarContract.Events.DURATION, "P1S");
                    intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                    intent.putExtra(CalendarContract.Events.HAS_ALARM, 1);

                    startActivity(intent);


//
//                    eventValues.put("rrule", "FREQ=DAILY;UNTIL=20170628" + dtUntil);
//                            eventValues.put(CalendarContract.Events.DTSTART, startMillis);
////                    eventValues.put(CalendarContract.Events.DTEND, endMillis);
//
//
////                    long endDate = startDate + 1000 * 60 * 60; // For next 1hr
//
//                    eventValues.put("dtstart", startMillis);
////                    eventValues.put("duration", "P1S");
//
//                    // values.put("allDay", 1); //If it is bithday alarm or such
//                    // kind (which should remind me for whole day) 0 for false, 1
//                    // for true
//                    eventValues.put("eventStatus", 0); // This information is
//                    // sufficient for most
//                    // entries tentative (0),
//                    // confirmed (1) or canceled
//                    // (2):
//                    TimeZone timeZone = TimeZone.getDefault();
//                    eventValues.put("eventTimezone", timeZone.getID());
//   /*Comment below visibility and transparency  column to avoid java.lang.IllegalArgumentException column visibility is invalid error */
//
//    /*eventValues.put("visibility", 3); // visibility to default (0),
//                                        // confidential (1), private
//                                        // (2), or public (3):
//    eventValues.put("transparency", 0); // You can control whether
//                                        // an event consumes time
//                                        // opaque (0) or transparent
//                                        // (1).
//      */
//                    eventValues.put("hasAlarm", 1); // 0 for false, 1 for true
//
//                    Uri eventUri = this.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
//                    long eventID = Long.parseLong(eventUri.getLastPathSegment());
//
//                    String reminderUriString = "content://com.android.calendar/reminders";
//
//                    ContentValues reminderValues = new ContentValues();
//
//                    reminderValues.put("event_id", eventID);
//                    reminderValues.put("minutes", 5); // Default value of the
//                    // system. Minutes is a
//                    // integer
//                    reminderValues.put("method", 1); // Alert Methods: Default(0),
//                    // Alert(1), Email(2),
//                    // SMS(3)
//
//                    Uri reminderUri = this.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);


                    Toast.makeText(this, "Ustawiono!", Toast.LENGTH_SHORT).show();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void onDateClicked(View view){
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy"; // your format
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                etUntil.setText(sdf.format(myCalendar.getTime()));
            }
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
