package com.hein;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.page_setting) {
//                // WARNING: TESTING PURPOSE ONLY
//                startActivity(new Intent(getApplicationContext(), TestActivity.class));
//                overridePendingTransition(0, 0);
//                return true;
//            }
//
//            return false;
//        });
    }
}
