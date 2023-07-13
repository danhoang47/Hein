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

public class DashboardActAdapter extends BaseAdapter {
    Context context;
    String listTransctionsId[];
    float listMoneyTransaction[];
    String listTransactTime[];

    String listBookingSize[];
    String listBookingColor[];
    String listBookingQuantity[];
    String listUserTransact[];

    String listProductTransact[];

    List<Booking> bookingList;

    LayoutInflater inflater;

    public DashboardActAdapter(Context context, String[] listTransctionsId, float[] listMoneyTransaction, String[] listTransactTime, String[] listUserTransact, String[] listProductTransact) {
        this.context = context;
        this.listTransctionsId = listTransctionsId;
        this.listMoneyTransaction = listMoneyTransaction;
        this.listTransactTime = listTransactTime;
        this.listUserTransact = listUserTransact;
        this.listProductTransact = listProductTransact;
        this.inflater = LayoutInflater.from(context);
    }

    public DashboardActAdapter(Context context, String[] listTransctionsId, float[] listMoneyTransaction, String[] listTransactTime) {
        this.context = context;
        this.listTransctionsId = listTransctionsId;
        this.listMoneyTransaction = listMoneyTransaction;
        this.listTransactTime = listTransactTime;
        this.inflater = LayoutInflater.from(context);
    }

    public DashboardActAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.inflater = LayoutInflater.from(context);

        int size = bookingList.size();
        listTransctionsId = new String[size];
        listMoneyTransaction = new float[size];
        listTransactTime = new String[size];
        listBookingSize = new String[size];
        listBookingColor = new String[size];
        listBookingQuantity = new String[size];

        for(int i =0 ;i<bookingList.size(); i++) {
            listTransctionsId[i] = bookingList.get(i).getProductName();
            listMoneyTransaction[i] = (int) bookingList.get(i).getTotalPrice();
            listTransactTime[i] = bookingList.get(i).getTimestamp();
            listBookingSize[i] = bookingList.get(i).getSize();
            listBookingColor[i] = bookingList.get(i).getColor();
            listBookingQuantity[i] = bookingList.get(i).getQuantity() + "";

        }
    }

    @Override
    public int getCount() {
        return listTransctionsId.length;
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
        view = inflater.inflate(R.layout.activity_dashboard_acts_list, null);
        TextView textViewTransactId= (TextView) view.findViewById(R.id.activities_transact_id);
        TextView textViewTransactTime = (TextView) view.findViewById(R.id.activities_transact_time);
        TextView textViewTransactAmount = (TextView) view.findViewById(R.id.activities_transact_amount);
        TextView textViewExtraInfo = (TextView) view.findViewById(R.id.activities_transact_type);

        textViewTransactId.setText("Order " + listTransctionsId[i]);
        textViewTransactTime.setText(listTransactTime[i]);
        textViewTransactAmount.setText("$" + listMoneyTransaction[i]);
        textViewExtraInfo.setText(listBookingColor[i] + ", " + listBookingSize[i] + " x " + listBookingQuantity[i]);
        return view;
    }
}
