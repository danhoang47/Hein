package com.hein.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hein.R;
import com.hein.entity.Booking;

import java.util.List;

public class DashboardNotiAdapter extends BaseAdapter {

    Context context;
    String listNotiContent[];
    String listNotiTime[];

    LayoutInflater inflater;

    public DashboardNotiAdapter(Context context, String[] listNotiContent, String[] listNotiTime) {
        this.context = context;
        this.listNotiContent = listNotiContent;
        this.listNotiTime = listNotiTime;
        this.inflater = LayoutInflater.from(context);
    }

    public DashboardNotiAdapter(Context context, List<Booking> listBooking) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        int size = listBooking.size();
        listNotiContent = new String[size];
        listNotiTime = new String[size];

        for(int i =0 ; i < listBooking.size(); i++) {
            String message =
                    listBooking.get(i).getQuantity() + " " +
                    listBooking.get(i).getProductName() + " with size " +
                    listBooking.get(i).getSize() + " and " + listBooking.get(i).getColor() + " had been ordered!!";

            listNotiContent[i] = message;
            listNotiTime[i] = listBooking.get(i).getTimestamp();
        }
    }

    @Override
    public int getCount() {
        return listNotiContent.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_dashboard_noti_list_activities, null);
        TextView textViewNotiContent = (TextView) view.findViewById(R.id.notification_content);
        TextView textViewNotiTime = (TextView) view.findViewById(R.id.notification_time);


        textViewNotiTime.setText(listNotiTime[i]);
        textViewNotiContent.setText(listNotiContent[i]);
        return view;
    }
}
