package com.hein.activities.fragment;


import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.hein.R;
import com.hein.activities.DynamicHeightListView;
import com.hein.activities.adapters.DashboardActAdapter;
import com.hein.entity.Booking;
import com.example.a2handee.activities.services.DashboardService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.List;

public class ActivitiesFragment extends Fragment {

    String dashboardTransactionId[] = {"9999", "4632", "1234", "9414", "2341", "3245"};
    String dashboardTransactTime[] ={"Fri 9 Apr, 2023", "Thu 12 Apr, 2023", "Fri 9 Apr, 2023", "Thu 12 Apr, 2023", "Fri 9 Apr, 2023", "Fri 9 Apr, 2023"};
    String dashBoardTransactAmount[] = {"90,00", "46,00", "50,34", "15,00", "23,00", "100,00"};

    FirebaseFirestore db;
    public static final int[] MATERIAL_COLORS = {
            rgb("#FF6B90"), rgb("#01A9F5"), rgb("#FF6B90"), rgb("#01A9F5")
    };

    DynamicHeightListView listView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        /*listView = (DynamicHeightListView) view.findViewById(R.id.dashboard_noti_list_view);
        DashboardNotiAdapter reviewsListAdapter = new DashboardNotiAdapter(getActivity().getApplicationContext(), dashboardNotiContent, dashboardNotiTime);
        listView.setAdapter(reviewsListAdapter);*/
        fetchData();
        return inflater.inflate(R.layout.fragment_activities, container, false);


    }

    public void fetchData() {
        db = FirebaseFirestore.getInstance();

        db.collection("Booking").orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        BarChart barChart = (BarChart) getView().findViewById(R.id.activities_bar_chart);
                        List<Booking> bookingList = queryDocumentSnapshots.toObjects(Booking.class);

                        try {
                            List<BarEntry> productsSelling = DashboardService.getListBarEntries(bookingList);

                            BarDataSet barDataSet = new BarDataSet(productsSelling, "Products");
                            barDataSet.setColors(MATERIAL_COLORS);
                            barDataSet.setValueTextColor(Color.BLACK);
                            barDataSet.setValueTextSize(16f);
                            barDataSet.setDrawValues(false);

                            BarData barData = new BarData(barDataSet);

                            barChart.setFitBars(true);
                            barChart.setData(barData);
                            barChart.getDescription().setText("");
                            barChart.animateY(2000);

                            /*-------------------------*/



                            listView = (DynamicHeightListView) getView().findViewById(R.id.dashboard_noti_list_view);
                            DashboardActAdapter reviewsListAdapter = new DashboardActAdapter(getActivity().getApplicationContext(), bookingList);
                            listView.setAdapter(reviewsListAdapter);


                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.i("message", e.getMessage());
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}