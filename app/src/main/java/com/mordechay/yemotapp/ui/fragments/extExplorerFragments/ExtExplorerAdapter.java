package com.mordechay.yemotapp.ui.fragments.extExplorerFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mordechay.yemotapp.ui.fragments.start.fiveFragment;
import com.mordechay.yemotapp.ui.fragments.start.oneFragment;
import com.mordechay.yemotapp.ui.fragments.start.twoFragment;

public class ExtExplorerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 3;

    public ExtExplorerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // החזר את הפרגמנט המתאים לפי המיקום
        switch (position) {
            case 0:
                return new ExtExplorerMangerFilesFragment();
            case 1:
                return new twoFragment();
            case 2:
                return new fiveFragment();
            default:
                return new ExtExplorerMangerFilesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
