package com.mordechay.yemotapp.ui.fragments.securingFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;

public class LoginLogFragment extends Fragment implements View.OnClickListener, OnRespondsYmtListener {

    public LoginLogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_log, container, false);

        Button btnKillAllSessions = v.findViewById(R.id.kill_all_session);
        btnKillAllSessions.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.kill_all_session){
            new SendRequestForYemotServer(getActivity(), this, "kill_all_session", Constants.URL_SECURING_KILL_ALL_SESSIONS + DataTransfer.getToken());
        }
    }

    @Override
    public void onSuccess(String result, String type) {

    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {

    }
}