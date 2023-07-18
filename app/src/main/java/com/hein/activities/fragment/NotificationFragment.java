package com.hein.activities.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hein.R;
import com.hein.activities.DynamicHeightListView;
import com.hein.activities.adapters.DashboardNotiAdapter;
import com.hein.entity.Booking;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class NotificationFragment extends Fragment {

    String dashboardNotiContent[] = {"This is a nice product lorem200 jiw jwx uqwbx quiw", "This is suck", "This is suck", "Danh bida de", "Android dau buoi", "Qua de voi huy"};
    String dashboardNotiTime[] ={"Le Duc", "Quoc Dat", "Huy hue", "promax", "Trong Hieu", "Phan Duc Thinh"};

    DynamicHeightListView listView;

    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fetchData();
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }


    public void fetchData() {
        db = FirebaseFirestore.getInstance();

        db.collection("Booking").orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Booking> bookingList = queryDocumentSnapshots.toObjects(Booking.class);



                        listView = (DynamicHeightListView) getView().findViewById(R.id.dashboard_noti_list_view);
                        DashboardNotiAdapter reviewsListAdapter = new DashboardNotiAdapter(getActivity().getApplicationContext(), bookingList);
                        listView.setAdapter(reviewsListAdapter);

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