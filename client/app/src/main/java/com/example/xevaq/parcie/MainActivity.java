package com.example.xevaq.parcie;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.transition.Scene;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;
    AppCompatActivity ctx;

    ScheduleClient scheduleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;
//
//        scheduleClient = new ScheduleClient(this);
//        scheduleClient.doBindService();

        SharedPreferences settings = getSharedPreferences("PARCIE_LOGIN_INFO", 0);

        GlobalConfig.getInstance().setLogin(settings.getString("login", ""));
        GlobalConfig.getInstance().setPassword(settings.getString("password", ""));
        queue = Volley.newRequestQueue(this);
        queue.start();

        isLoggedIn();


//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Intent intent = new Intent(this, AddActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),intent,0);
//
//
//        Intent myIntent = new Intent(this , NotificationManager.class);
//        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 22);
//        calendar.set(Calendar.MINUTE, 21);
//        calendar.set(Calendar.SECOND, 00);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES , pendingIntent);
//
//
//        Notification n =new Notification.Builder(this).
//                setContentTitle("tytuł")
//                .setContentText("text")
//                .setSmallIcon(R.drawable.ikona)
//                .setContentIntent(pi)
//                .setAutoCancel(true)
//                .addAction(R.drawable.ikona, "Dodaj", pi).build();
//
//        nm.notify(0, n);


//        Intent i = new Intent(this, LoginActivity.class);
//        startActivity(i);
    }

//    @Override
//    protected void onStop() {
//        // When our activity is stopped ensure we also stop the connection to the service
//        // this stops us leaking our activity into the system *bad*
//        if (scheduleClient != null)
//            scheduleClient.doUnbindService();
//        super.onStop();
//    }

    private void setButtonsVisibility() {
        boolean logged = GlobalConfig.getInstance().isLoggedIn();

        findViewById(R.id.btnMainAdd).setVisibility(logged ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnMainBrowse).setVisibility(logged ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnMainReport).setVisibility(logged ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnMainSchedule).setVisibility(logged ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnMainLogout).setVisibility(logged ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnMainLogin).setVisibility(logged ? View.GONE : View.VISIBLE);
        findViewById(R.id.btnMainRegister).setVisibility(logged ? View.GONE : View.VISIBLE);
    }

    private void isLoggedIn() {
        JSONObject body = new JSONObject();
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                GlobalConfig.getInstance().getEndpoint() + "/blood/",
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        GlobalConfig.getInstance().setLoggedIn(true);
                        setButtonsVisibility();
                        Toast.makeText(ctx, "Zalogowano jako "+GlobalConfig.getInstance().getLogin(), Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        GlobalConfig.getInstance().setLoggedIn(false);
                        setButtonsVisibility();
                    }
                }
        ) {
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

        req.setRetryPolicy(new DefaultRetryPolicy(60_000, 1, 1f));

        queue.start();
        queue.add(req);
    }

    public void btnMainBrowseClick(View view) {
        Intent i = new Intent(this, BrowseActivity.class);
        startActivity(i);
    }

    public void btnMainAddClick(View view) {
        Intent i = new Intent(this, AddActivity.class);
        startActivity(i);
    }

    public void btnMainReportClick(View view) {
        Intent i = new Intent(this, ReportActivity.class);
        startActivity(i);
    }

    public void btnMainScheduleClick(View view) {
        Intent i = new Intent(this, ScheduleActivity.class);
        startActivityForResult(i, 0);
        setButtonsVisibility();

       /* Calendar cnow = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
//        c.set(2016, 12, 15);
//        c.set(Calendar.HOUR_OF_DAY, 22);
//        c.set(Calendar.MINUTE, 21);
//        c.set(Calendar.SECOND, 50);

        c.add(Calendar.SECOND, 3);


        scheduleClient.setAlarmForNotification(c);
        Toast.makeText(this, "set", Toast.LENGTH_SHORT).show();*/
    }

    public void btnMainLoginClick(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, 0);
        setButtonsVisibility();
    }

    public void btnMainRegisterClick(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivityForResult(i, 0);
        setButtonsVisibility();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setButtonsVisibility();
    }

    public void btnLogoutClick(View view) {
        GlobalConfig.getInstance().setLoggedIn(false);
        GlobalConfig.getInstance().setLogin("");
        GlobalConfig.getInstance().setPassword("");
        SharedPreferences settings = getSharedPreferences("PARCIE_LOGIN_INFO", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("login", GlobalConfig.getInstance().getLogin());
        editor.putString("password", GlobalConfig.getInstance().getPassword());
        editor.putBoolean("logged in", GlobalConfig.getInstance().isLoggedIn());
        Toast.makeText(ctx, "Wylogowano", Toast.LENGTH_SHORT).show();

        // Commit the edits!
        editor.commit();

        setButtonsVisibility();
    }
}
