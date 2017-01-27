package com.example.xevaq.parcie;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.xevaq.parcie.Measurement;
import com.example.xevaq.parcie.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static android.view.View.GONE;

/**
 * Created by xevaq on 11-Dec-16.
 */

public class MeasurementAdapter extends ArrayAdapter<Measurement> {
    Context context;
    int layoutResourceId;
    List<Measurement> data = null;

    public MeasurementAdapter(Context context, int layoutResourceId, List<Measurement> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MeasurementHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MeasurementHolder();
            holder.tvDate = (TextView) row.findViewById(R.id.tvDate);
            holder.tvDiastolic = (TextView) row.findViewById(R.id.tvDiastolic);
            holder.tvSystolic = (TextView) row.findViewById(R.id.tvSystolic);
            holder.tvPulse = (TextView) row.findViewById(R.id.tvPulse);
            holder.id = 0;

            row.setTag(holder);
        } else {
            holder = (MeasurementHolder)row.getTag();
        }

        Measurement m = data.get(position);
        ;
        Calendar cal = Calendar.getInstance();
        cal.setTime(m.getDate());
        cal.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY hh:mm");
        holder.tvDate.setText("");
        holder.tvSystolic.setText("Tętno: " + String.valueOf(m.getPulse()) +" Skurczowe: " + String.valueOf(m.getSystolic())+ " " + "Rozkurczowe: " + String.valueOf(m.getDiastolic()));
        holder.tvDiastolic.setText("");
        holder.tvPulse .setText(m.getOnlyDate() + " " + m.getOnlyTime());
        holder.tvDate.setVisibility(GONE);
        holder.tvDiastolic.setVisibility(GONE);


        holder.id = m.getId();

        return row;

    }

    static class MeasurementHolder {
        TextView tvPulse;
        TextView tvSystolic;
        TextView tvDiastolic;
        TextView tvDate;
        int id;
    }
}
