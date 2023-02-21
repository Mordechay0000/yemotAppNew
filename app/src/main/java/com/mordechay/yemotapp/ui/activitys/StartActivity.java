package com.mordechay.yemotapp.ui.activitys;


import me.relex.circleindicator.CircleIndicator3;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.ui.fragments.start.*;


public class StartActivity extends FragmentActivity {

    private static final int NUM_PAGES = 5;

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.viewPager2_start);


        FragmentStateAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        CircleIndicator3 indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        pagerAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver());
}






    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new oneFragment();
                case 1:
                    return new twoFragment();
                case 2:
                    return new threeFragment();
                case 3:
                    return new fourFragment();
                case 4:
                    return new fiveFragment();
                default:return new oneFragment();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}