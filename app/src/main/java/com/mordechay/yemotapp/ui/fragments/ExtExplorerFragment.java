package com.mordechay.yemotapp.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.ui.fragments.extExplorerFragments.ExtExplorerMangerFilesFragment;
import com.mordechay.yemotapp.ui.fragments.start.fiveFragment;
import com.mordechay.yemotapp.ui.fragments.start.oneFragment;

import java.util.Objects;

public class ExtExplorerFragment extends Fragment {

    private final Fragment[] tabsFragments = {new ExtExplorerMangerFilesFragment(), new oneFragment(), new fiveFragment()};



    public ExtExplorerFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ext_explorer, container, false);


        getChildFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, tabsFragments[0]).commit();


        TabLayout tabLayout = v.findViewById(R.id.ext_explorer_fragment_tab_layout);

        // החלפת הפרגמנטים עם הטאבים
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                selectedFragment = tabsFragments[tab.getPosition()];
                if (selectedFragment != null) {
                    getChildFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selectedFragment).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // כדי להציג את הפרגמנט הראשון לפני לחיצה על טאבים
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();

        return v;
    }
}