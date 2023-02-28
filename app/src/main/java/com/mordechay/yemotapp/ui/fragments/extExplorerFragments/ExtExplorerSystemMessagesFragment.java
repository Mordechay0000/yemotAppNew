package com.mordechay.yemotapp.ui.fragments.extExplorerFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;

public class ExtExplorerSystemMessagesFragment extends Fragment {
    private String thisWhat;

    public ExtExplorerSystemMessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ext_explorer_system_messages, container, false);

        thisWhat = DataTransfer.getThisWhat();
        if(thisWhat == null){
            thisWhat = "ivr2:/";
        }


        return v;
    }
}