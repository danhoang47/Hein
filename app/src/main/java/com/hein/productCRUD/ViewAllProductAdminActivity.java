package com.hein.productCRUD;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hein.R;
import com.hein.activities.DynamicHeightListView;
import com.hein.activities.adapters.DashboardNotiAdapter;
import com.hein.entity.Booking;
import com.hein.entity.Product;

import java.util.List;

public class ViewAllProductAdminActivity extends AppCompatActivity {

    DynamicHeightListView listView;

    FirebaseFirestore db;

    AppCompatButton addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_product_admin);
        addBtn = findViewById(R.id.btn_product_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddNewItems_Activity.class);
                startActivity(intent);
            }
        });

        fetchData();
    }

    public void fetchData() {
        db = FirebaseFirestore.getInstance();

        /*.orderBy("timestamp", Query.Direction.DESCENDING)*/
        db.collection("Product")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Product> bookingList = queryDocumentSnapshots.toObjects(Product.class);

                        listView = (DynamicHeightListView) findViewById(R.id.list_product_view_admin);
                        ViewAllProductAdminAdapter reviewsListAdapter = new ViewAllProductAdminAdapter(getApplicationContext(), bookingList);
                        listView.setAdapter(reviewsListAdapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.i("message", e.getMessage());
                        Toast.makeText(ViewAllProductAdminActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}