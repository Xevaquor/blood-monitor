package com.example.xevaq.parcie;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    EditText etDate;
    EditText etTime;
    EditText etPulse, etSystolic, etDiastolic;
    private int measurementID;
    RequestQueue queue;
    TextView tvStatus;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etDate = (EditText)findViewById(R.id.etDateAdd);
        etTime = (EditText)findViewById(R.id.etTimeAdd);
        etPulse = (EditText)findViewById(R.id.etAddPulse);
        etSystolic = (EditText)findViewById(R.id.etAddSystolic);
        etDiastolic = (EditText)findViewById(R.id.etAddDiastolic);
        tvStatus = (TextView)findViewById(R.id.tvAddStatus);

        Context ctx = this;

        etPulse.requestFocus();

        queue = Volley.newRequestQueue(this);
        setNow();
    }

    public void setNow(){
        Calendar now = Calendar.getInstance();
        String myFormat = "dd-MM-yyyy"; // your format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etDate.setText(sdf.format(now.getTime()));
        myFormat = "HH:mm"; // your format
        sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etTime.setText(sdf.format(now.getTime()));
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

                etDate.setText(sdf.format(myCalendar.getTime()));
            }
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void onTimeClicked(View view){
        final Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                myCalendar.set(Calendar.MINUTE, minute);
                String myFormat = "HH:mm"; // your format
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                etTime.setText(sdf.format(myCalendar.getTime()));
            }
        };
        new TimePickerDialog(this, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),true).show();

    }

    public void onBtnClicked(final View view){
        JSONObject body = new JSONObject();
        try {
            MeasurementValidator mv = new MeasurementValidator();
            if(!mv.isValid(etPulse.getText().toString(),
                    etSystolic.getText().toString(),
                    etDiastolic.getText().toString(),
                    etDate.getText().toString(),
                    etTime.getText().toString()
            )){
                Toast.makeText(this, "Niepoprawne dane", Toast.LENGTH_SHORT).show();
               return;
            }

            body.put("pulse", etPulse.getText().toString());
            body.put("systolic", etSystolic.getText().toString());
            body.put("diastolic", etDiastolic.getText().toString());
            body.put("id", measurementID);
            body.put("date", etDate.getText().toString() + " " + etTime.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                GlobalConfig.getInstance().getEndpoint()+"/blood/create",
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        Toast.makeText(view.getContext(), "Dodano", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(view.getContext(), "Błąd  :c", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", GlobalConfig.getInstance().getLogin(), GlobalConfig.getInstance().getPassword());
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                params.put("Accept", "application/json");

                return params;
            }

        };

        req.setRetryPolicy(new DefaultRetryPolicy(60_000,1,1f));

        queue.start();
        queue.add(req);

    }
}
