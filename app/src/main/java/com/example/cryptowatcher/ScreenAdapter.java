package com.example.cryptowatcher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ScreenAdapter extends FragmentStateAdapter {
    private static final int NUM_CATEGORIES = 2;
    public ScreenAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PriceFragment();
            default:
                return new TempFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_CATEGORIES;
    }
}