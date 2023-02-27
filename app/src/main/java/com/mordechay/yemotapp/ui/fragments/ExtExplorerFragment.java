package com.mordechay.yemotapp.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.ui.fragments.extExplorerFragments.ExtExplorerAdapter;
import com.mordechay.yemotapp.ui.fragments.securingFragments.SecuringAdapter;

import java.util.List;

public class ExtExplorerFragment extends Fragment {

    private final String[] tabsText = {"ניהול קבצים", "הגדרות מתקדמות", "הודעות מערכת"};



    public ExtExplorerFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ext_explorer, container, false);



        ViewPager2 viewPager = v.findViewById(R.id.ext_explorer_fragment_view_pager2);
        ExtExplorerAdapter my = new ExtExplorerAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setAdapter(my);
        viewPager.setOffscreenPageLimit(1);


        TabLayout tabLayout = v.findViewById(R.id.ext_explorer_fragment_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabsText[position])
        ).attach();


        return v;
    }



}