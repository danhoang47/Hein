package com.hein.ordered_product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hein.R;
import com.hein.entity.Booking;

import java.util.List;

public class OrderActivity extends AppCompatActivity {
    OrderedProductAdapter orderedProductAdapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        initOrderedProductAdapter(userId);
    }

    private void initOrderedProductAdapter(String userId) {
        RecyclerView recyclerView = findViewById(R.id.order_product_rv);
        db.collection("Booking")
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", 1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Booking> orders = task.getResult().toObjects(Booking.class);
                        orderedProductAdapter = new OrderedProductAdapter(getApplicationContext(), orders);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        recyclerView.setAdapter(orderedProductAdapter);
                        recyclerView.setLayoutManager(linearLayoutManager);
                    }
                });
    }

}