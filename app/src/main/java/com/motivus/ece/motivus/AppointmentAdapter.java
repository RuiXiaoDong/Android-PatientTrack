package com.motivus.ece.motivus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dongx on 2015-02-19.
 */
public class AppointmentAdapter extends ArrayAdapter {
    private Context context;

    public AppointmentAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    private class ViewHolder{
        TextView dateText;
        TextView titleText;
        CheckBox checkBox;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Appointment item = (Appointment)getItem(position);
        View viewToUse = null;
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            viewToUse = mInflater.inflate(R.layout.fragment_row, null);
            holder = new ViewHolder();
            holder.dateText = (TextView)viewToUse.findViewById(R.id.text_date);
            holder.titleText = (TextView)viewToUse.findViewById(R.id.text_title);
            holder.checkBox = (CheckBox)viewToUse.findViewById(R.id.checkBox_done);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        holder.dateText.setText(item.date + " " + item.time);
        holder.titleText.setText(item.title);
        holder.checkBox.setChecked(item.done == 1);
        return viewToUse;
    }


}
