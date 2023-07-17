package com.hein.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hein.ConsumerAsyncTask;
import com.hein.R;
import com.hein.activities.DetailedActivity;
import com.hein.entity.Booking;
import com.hein.home.HomeActivity;
import com.hein.home.OnProductClickListener;
import com.hein.ordered_product.OrderActivity;
import com.hein.ordered_product.OrderedProductAdapter;
import com.hein.shoppingcart.adapter.CartAdapter;

import java.util.HashMap;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity implements OnProductClickListener {
    private CartAdapter cartAdapter;
    private TextView totalCartPriceTv, textView;
    private AppCompatButton checkoutBtn;
    private CardView cardView;
    CartViewModel cartViewModel;
    FirebaseFirestore db;
    private static String userId;
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        db = FirebaseFirestore.getInstance();

        totalCartPriceTv = findViewById(R.id.cartActivityTotalPriceTv);
        textView = findViewById(R.id.textView2);
        cardView = findViewById(R.id.cartActivityCardView);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        observerCartViewModel();

        checkoutBtn = findViewById(R.id.cartActivityCheckoutBtn);
        checkoutBtn.setOnClickListener(v -> checkOutProductsInCart());

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        Log.i("MSG", userId);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initNavigationBoard();
        initCartAdapter();
    }

    public void initNavigationBoard() {

        bottomNavigationView.setSelectedItemId(R.id.page_shopping_cart);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.page_setting) {
                // WARNING: TESTING PURPOSE ONLY
//                startActivity(new Intent(getApplicationContext(), TestActivity.class));
//                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.page_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                overridePendingTransition(0, 0);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    @Override
    public void onProductClick(String productId) {
        Intent detailIntent = new Intent(getApplicationContext(), DetailedActivity.class);
        detailIntent.putExtra("productId", productId);
        startActivity(detailIntent);
    }

    private void initCartAdapter() {
        RecyclerView recyclerView = findViewById(R.id.cartRecyclerView);
        db.collection("Booking")
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", 0)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Booking> orders = task.getResult().toObjects(Booking.class);
                        Log.i("MSG", "Shopping Cart size: " + orders.size());

                        cartAdapter = new CartAdapter(ShoppingCartActivity.this, orders, cartViewModel, (productId -> {
                            onProductClick(productId);
                        }));
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        recyclerView.setAdapter(cartAdapter);
                        recyclerView.setLayoutManager(linearLayoutManager);
                    }
                });
    }

    private void checkOutProductsInCart() {
        if (cartViewModel.isCartItemChanged()) {
            syncCartWithDatabase();
        }

        if (cartViewModel.orders.getValue().size() != 0) {
            cartViewModel.orders.getValue()
                    .stream()
                    .forEach(order -> {
                        ConsumerAsyncTask<Booking> consumerAsyncTask = new ConsumerAsyncTask<>(this, (o) -> {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            HashMap<String, Object> set = new HashMap<>();
                            set.put("type", 1);

                            db.collection("Booking")
                                    .document(order.getId())
                                    .update(set);

                        });

                        consumerAsyncTask.execute(order);
                    });

            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            Toast.makeText(this,"Order success", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Your shopping cart is empty", Toast.LENGTH_SHORT).show();
        }
    }


    private void syncCartWithDatabase() {
        if (cartViewModel.isCartItemChanged()) {
            cartViewModel.orders.getValue()
                .stream()
                .forEach(order -> {
                ConsumerAsyncTask<Booking> consumerAsyncTask = new ConsumerAsyncTask<>(this, (o) -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    HashMap<String, Object> set = new HashMap<>();
                    set.put("quantity", order.getQuantity());

                    db.collection("Booking")
                            .document(o.getId())
                            .update(set)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.i("MSG", "Updated cart !!!");
                                }
                            });

                });

                consumerAsyncTask.execute(order);
            });

            cartViewModel.deletedOrders.getValue().stream().forEach(orderId -> {
                ConsumerAsyncTask<String> consumerAsyncTask = new ConsumerAsyncTask<>(this, (o) -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("Booking")
                            .document(o)
                            .delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.i("MSG", "Updated cart !!!");
                                }
                            });

                });

                consumerAsyncTask.execute(orderId);
            });
        }
    }

    private void observerCartViewModel() {
        cartViewModel.totalPrice.observe(this, observer -> {
            totalCartPriceTv.setText("$" + cartViewModel.getTotalPrice());
        });
    }

    @Override
    public void onBackPressed() {
        if (cartViewModel.isCartItemChanged()) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Confirm discard changes")
                    .setMessage("Are you sure you want to discard all change to Shopping Cart")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        super.onBackPressed();
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        syncCartWithDatabase();
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MSG", "ShoppingCartActivity STOP");
        syncCartWithDatabase();
    }
}