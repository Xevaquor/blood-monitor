package com.example.xevaq.parcie;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.net.URI;
import java.security.GuardedObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    EditText etFrom, etTo;
    RequestQueue queue;
    ImageView iv;

    String IMAGE_URL = GlobalConfig.getInstance().getEndpoint()+ "/blood/report/plot/?username="+GlobalConfig.getInstance().getLogin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        etFrom = (EditText) findViewById(R.id.etReportFrom);
        etTo = (EditText) findViewById(R.id.etReportTo);
        iv = (ImageView) findViewById(R.id.imageView);

        queue = Volley.newRequestQueue(this);
        queue.start();

        ImageLoader mImageLoader;
        NetworkImageView mNetworkImageView;

// Get the NetworkImageView that will display the image.
//        mNetworkImageView = (NetworkImageView) findViewById(R.id.networkImageView);

// Get the ImageLoader through your singleton class.
//        mImageLoader = MySingleton.getInstance(this).getImageLoader();

// Set the URL of the image that should be loaded into this view, and
// specify the ImageLoader that will be used to make the request.
//        mNetworkImageView.setImageUrl(IMAGE_URL, mImageLoader);
    }

    public void btnGetReportClick(View view) {
        String builtUrl = IMAGE_URL + "&from="+etFrom.getText().toString()+"&to="+etTo.getText().toString();
        ImageRequest request = new ImageRequest(builtUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        iv.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        //iv.setImageResource(R.drawable.c);
                        System.out.println(error);
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(15_000,2,1));
        queue.add(request);
    }


    public void onDateClicked2(final View viewOrig) {
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
                ((EditText) viewOrig).setText(sdf.format(myCalendar.getTime()));
            }
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
