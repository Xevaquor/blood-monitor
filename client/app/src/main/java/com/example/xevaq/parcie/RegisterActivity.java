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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    EditText etLogin;
    EditText etPassword;
    EditText etConfirmPassword;
    Button btnRegister;
    AppCompatActivity ctx;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ctx = this;
        etLogin = (EditText) findViewById(R.id.etRegisterLogin);
        etPassword = (EditText) findViewById(R.id.etRegisterPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etRegisterConfirmPassword);
        btnRegister = (Button) findViewById(R.id.btnRegisterRegister);
        queue = Volley.newRequestQueue(this);
        queue.start();
    }

    public void onBtnRegister(View view) {

        try {
            if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ctx);
                dlgAlert.setMessage("hasła nie są identyczne! ");
//                        dlgAlert.setTitle("App Title");
                dlgAlert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }

            JSONObject body = new JSONObject();
            body.put("username", etLogin.getText().toString());
            body.put("password", etPassword.getText().toString());

            GlobalConfig.getInstance().setLogin(etLogin.getText().toString());
            GlobalConfig.getInstance().setPassword(etPassword.getText().toString());

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST,
                    GlobalConfig.getInstance().getEndpoint() + "/user/signup",
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

                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ctx);
                            dlgAlert.setMessage("Zarejestrowano! ");
//                        dlgAlert.setTitle("App Title");
                            dlgAlert.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //dismiss the dialog
                                            finish();
                                        }
                                    });
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            GlobalConfig.getInstance().setLoggedIn(false);
                            SharedPreferences settings = getSharedPreferences("PARCIE_LOGIN_INFO", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("logged in", GlobalConfig.getInstance().isLoggedIn());
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ctx);
                            dlgAlert.setMessage("Nie udało się zarejestrować!");
//                        dlgAlert.setTitle("App Title");
                            dlgAlert.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //dismiss the dialog
                                        }
                                    });
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
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
            queue.add(req);
        } catch (Exception e) {

        }
    }
}
