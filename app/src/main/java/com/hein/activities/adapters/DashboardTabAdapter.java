package com.hein.activities.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hein.activities.fragment.ActivitiesFragment;
import com.hein.activities.fragment.IncomeFragment;
import com.hein.activities.fragment.NotificationFragment;

public class DashboardTabAdapter extends FragmentStateAdapter {


    public DashboardTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new IncomeFragment();
            case 1: return new ActivitiesFragment();
            case 2: return new NotificationFragment();
            default: return new IncomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
