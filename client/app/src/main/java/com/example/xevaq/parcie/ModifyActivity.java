package com.example.xevaq.parcie;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModifyActivity extends AppCompatActivity {

    EditText etDate;
    EditText etTime;
    EditText etPulse, etSystolic, etDiastolic;
    private int measurementID;
    RequestQueue queue;
    TextView tvStatus;
    AppCompatActivity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        ctx = this;

        etDate = (EditText) findViewById(R.id.etDateModify);
        etTime = (EditText) findViewById(R.id.etTimeModify);
        etPulse = (EditText) findViewById(R.id.etModifyPulse);
        etSystolic = (EditText) findViewById(R.id.etModifySystolic);
        etDiastolic = (EditText) findViewById(R.id.etModifyDiastolic);
        tvStatus = (TextView) findViewById(R.id.tvModifyStatus);

        queue = Volley.newRequestQueue(this);
        measurementID = getIntent().getIntExtra("ENTRY_ID", 0);

        queue.start();
        loadData();
    }

    public void loadData() {
        JSONObject body = new JSONObject();
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                GlobalConfig.getInstance().getEndpoint()+"/blood/" + measurementID,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        fillUI(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "jaro", "jaro");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                params.put("Accept", "application/json");

                return params;
            }

        };

        req.setRetryPolicy(new DefaultRetryPolicy(10_000, 3, 1f));

        queue.start();
        queue.add(req);
    }

    private void fillUI(JSONObject response) {
        try {
            etSystolic.setText(response.getString("systolic"));
            etPulse.setText(response.getString("pulse"));
            etDiastolic.setText(response.getString("diastolic"));

            etDate.setText(response.getString("only_date"));
            etTime.setText(response.getString("only_time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setNow() {
        Calendar now = Calendar.getInstance();
        String myFormat = "dd MMMM yyyy"; // your format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etDate.setText(sdf.format(now.getTime()));
        myFormat = "HH:mm"; // your format
        sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etTime.setText(sdf.format(now.getTime()));
    }

    public void onDateClicked(View view) {
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

    public void onTimeClicked(View view) {
        final Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                myCalendar.set(Calendar.MINUTE, minute);
                String myFormat = "HH:mm"; // your format
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                etTime.setText(sdf.format(myCalendar.getTime()));
            }
        };
        new TimePickerDialog(this, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();

    }

    public void onBtnModifyClicked(View view) {
        JSONObject body = new JSONObject();
        try {
            MeasurementValidator mv = new MeasurementValidator();
            if (!mv.isValid(etPulse.getText().toString(),
                    etSystolic.getText().toString(),
                    etDiastolic.getText().toString(),
                    etDate.getText().toString(),
                    etTime.getText().toString()
            )) {
                Toast.makeText(ctx, "Nieproawne dane", Toast.LENGTH_SHORT).show();

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
                GlobalConfig.getInstance().getEndpoint() +"/blood/" + measurementID,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ctx, "Uaktualniono", Toast.LENGTH_SHORT).show();

                        System.out.println(response.toString());
                        setResult(1, new Intent());
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                        Toast.makeText(ctx, "Fail  :c", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "jaro", "jaro");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                params.put("Accept", "application/json");

                return params;
            }

        };

        req.setRetryPolicy(new DefaultRetryPolicy(10_000, 1, 1f));

        queue.add(req);

    }

    public void onBtnDeleteClicked(View view) {
        JSONObject body = new JSONObject();


        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                GlobalConfig.getInstance().getEndpoint()+"/blood/delete/" + measurementID,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ctx, "usunięto", Toast.LENGTH_SHORT).show();


                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Błąd  :c", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "jaro", "jaro");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                params.put("Accept", "application/json");

                return params;
            }

        };

        req.setRetryPolicy(new DefaultRetryPolicy(10_000, 1, 1f));

        queue.add(req);
    }
}
