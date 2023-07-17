package com.hein.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.hein.R;
import com.google.android.material.tabs.TabLayout;
import com.hein.activities.adapters.DashboardTabAdapter;

public class DashboardActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    DashboardTabAdapter dashboardTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager2 = (ViewPager2) findViewById(R.id.view_pager);

        dashboardTabAdapter = new DashboardTabAdapter(this);

        viewPager2.setAdapter(dashboardTabAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

    }
}