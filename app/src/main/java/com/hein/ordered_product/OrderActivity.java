package com.hein.ordered_product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hein.R;
import com.hein.account.AccountActivity;
import com.hein.entity.Booking;
import com.hein.home.HomeActivity;
import com.hein.shoppingcart.ShoppingCartActivity;

import java.util.List;

public class OrderActivity extends AppCompatActivity {
    OrderedProductAdapter orderedProductAdapter;
    FirebaseFirestore db;

    BottomNavigationView bottomNavigationView;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        initOrderedProductAdapter(userId);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initNavigationBoard();
    }

    public void initNavigationBoard() {

        bottomNavigationView.setSelectedItemId(R.id.page_setting);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.page_setting) {
                // WARNING: TESTING PURPOSE ONLY
                    Intent intent = new Intent(this, AccountActivity.class);
                    intent.putExtra("userId", userId);
                    overridePendingTransition(0, 0);
                    startActivity(intent);
//                overridePendingTransition(0, 0);
                    return true;
            }
            else if (itemId == R.id.page_shopping_cart) {

                Intent intent = new Intent(this, ShoppingCartActivity.class);
                intent.putExtra("userId", userId);
                overridePendingTransition(0, 0);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.page_home) {
                // NOTE: Account Setting Intent
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("userId", userId);
                overridePendingTransition(0, 0);
                startActivity(intent);
                return true;
            }

            return false;
        });
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
                        Log.i("MSG", "Order size: " + orders.size());

                        orderedProductAdapter = new OrderedProductAdapter(getApplicationContext(), orders);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        recyclerView.setAdapter(orderedProductAdapter);
                        recyclerView.setLayoutManager(linearLayoutManager);
                    }
                });
    }

}