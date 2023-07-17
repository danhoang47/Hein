package com.hein.account;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hein.R;
import com.hein.entity.User;
import com.hein.home.HomeActivity;
import com.hein.home.login.LoginDialogFragment;
import com.hein.ordered_product.OrderActivity;
import com.hein.shoppingcart.ShoppingCartActivity;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AccountActivity extends AppCompatActivity  {
//    private ActivityMainBinding binding;
    FirebaseFirestore dbroot;
    protected ImageView avatar;
    protected String user_document=null;
    protected TextView headname;
    TextView job;
    BottomNavigationView  bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initNavigationBoard();
        dbroot=FirebaseFirestore.getInstance();
         avatar = findViewById(R.id.avatarImage);
         headname= findViewById(R.id.UsernameView);
         job= findViewById(R.id.JobView);
        user_document=getIntent().getStringExtra("userId");
        configureDetailButton();
        fetchUser();

    }
    public void initNavigationBoard() {

        bottomNavigationView.setSelectedItemId(R.id.page_setting);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.page_setting) {
                // WARNING: TESTING PURPOSE ONLY
                if (user_document == null) {
                    showLoginDialog();
                    return false;
                } else {
                    Intent intent = new Intent(this, AccountActivity.class);
                    intent.putExtra("userId", user_document);
                    overridePendingTransition(0, 0);
                    startActivity(intent);
//                overridePendingTransition(0, 0);
                    return true;}
            } else if (itemId == R.id.page_shopping_cart) {
                if (user_document == null) {
                    showLoginDialog();
                    return false;
                } else {
                    Intent intent = new Intent(this, ShoppingCartActivity.class);
                    intent.putExtra("userId", user_document);
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    return true;
                }
            } else if (itemId == R.id.page_home) {
                if (user_document == null) {
                    showLoginDialog();
                    return false;
                } else {
                    // NOTE: Account Setting Intent
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("userId", user_document);
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    return true;
                }
            }

            return false;
        });
    }
    protected void showLoginDialog() {
        LoginDialogFragment dialog = new LoginDialogFragment();

        dialog.show(getSupportFragmentManager(), "loginDialog");
    }

    public void configureDetailButton(){
        LinearLayout detailButton = (LinearLayout) findViewById(R.id.button_detail);
        LinearLayout oders = (LinearLayout) findViewById(R.id.Orders);
        oders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, OrderActivity.class);
                intent.putExtra("userId",user_document);
                startActivity(intent);
            }
        });
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this,AccountDetail.class);
                intent.putExtra("userId",user_document);
                startActivity(intent);
            }
        });
    }

    public void fetchUser(){
        DocumentReference document =dbroot.collection("User").document(user_document);
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                headname.setText(documentSnapshot.getString("name"));
                Glide.with(getApplicationContext()).load(documentSnapshot.getString("avatar")).into(avatar);
                job.setText(documentSnapshot.getString("job"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
