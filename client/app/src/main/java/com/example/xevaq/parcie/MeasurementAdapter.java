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

import java.util.List;

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
        holder.tvDate.setText(m.getDate().toString());
        holder.tvSystolic.setText("Skurczowe: " + String.valueOf(m.getSystolic()));
        holder.tvDiastolic.setText("Rozkurczowe: " + String.valueOf(m.getDiastolic()));
        holder.tvPulse .setText("Tętno: " + String.valueOf(m.getPulse()));
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
