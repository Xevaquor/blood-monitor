package com.example.xevaq.parcie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etLogin;
    EditText etPassword;
    Button btnLogin;
    AppCompatActivity ctx;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ctx = this;

        etLogin = (EditText) findViewById(R.id.etLoginLogin);
        etPassword = (EditText) findViewById(R.id.etLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLoginLogin);
        queue = Volley.newRequestQueue(this);
        queue.start();
    }

    public void onBtnLoginClick(View view){
        JSONObject body = new JSONObject();

        GlobalConfig.getInstance().setLogin(etLogin.getText().toString());
        GlobalConfig.getInstance().setPassword(etPassword.getText().toString());

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                GlobalConfig.getInstance().getEndpoint() + "/blood/",
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        GlobalConfig.getInstance().setLoggedIn(true);
                        SharedPreferences settings = getSharedPreferences("PARCIE_LOGIN_INFO", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("login", GlobalConfig.getInstance().getLogin());
                        editor.putString("password", GlobalConfig.getInstance().getPassword());
                        editor.putBoolean("logged in", GlobalConfig.getInstance().isLoggedIn());

                        // Commit the edits!
                        editor.commit();


                        Toast.makeText(ctx, "Zalogowano", Toast.LENGTH_SHORT).show();
//                        dlgAlert.setTitle("App Title");
                        finish();

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        GlobalConfig.getInstance().setLoggedIn(false);
                        SharedPreferences settings = getSharedPreferences("PARCIE_LOGIN_INFO", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("logged in", GlobalConfig.getInstance().isLoggedIn());
                        Toast.makeText(ctx, "Porażka logowania :x", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s",GlobalConfig.getInstance().getLogin(),GlobalConfig.getInstance().getPassword());
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
