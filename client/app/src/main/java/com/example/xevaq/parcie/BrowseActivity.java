package com.example.xevaq.parcie;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowseActivity extends AppCompatActivity {

    RequestQueue queue;

    List<Measurement> results = new ArrayList<Measurement>();
    Intent openDetails;
    MeasurementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_browse);
        openDetails = new Intent(this, ModifyActivity.class);
        adapter = new MeasurementAdapter(this, R.layout.list_item, results);

        update(adapter);

        ListView lvItems = (ListView)findViewById(R.id.lvItems);
        lvItems.setAdapter(adapter);

        queue.start();

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                MeasurementAdapter.MeasurementHolder holder = (MeasurementAdapter.MeasurementHolder) view.getTag();
                openDetails.putExtra("ENTRY_ID", holder.id);
                startActivityForResult(openDetails,1);
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        update(adapter);
    }

    @NonNull
    private void update(final MeasurementAdapter adapter) {
        JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.GET,
                    GlobalConfig.getInstance().getEndpoint()+"/blood/",
                    null,
            new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        results.clear();
                        JSONArray rows = (JSONArray) response.get("rows");
                        for (int i = 0; i < rows.length(); i++){
                            JSONObject o = (JSONObject) rows.get(i);
                            Date d = new Date();
                            results.add(new Measurement(o.getInt("id"),
                                    o.getInt("pulse"),
                                    o.getInt("systolic"),
                                    o.getInt("diastolic"),
                                    new Date(o.getString("date"))

                                    ));
                        adapter.notifyDataSetChanged();

                        }
                        System.out.println("xd");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("O SIET");
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

        req.setRetryPolicy(new DefaultRetryPolicy(10_000,10,1.5f));
        queue.add(req);
    }
}
