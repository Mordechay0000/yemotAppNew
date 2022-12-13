package com.mordechay.yemotapp.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.list.DataModel;

import java.util.ArrayList;


public class sipCallsFragment extends Fragment implements sendApiRequest.RespondsListener, SwipeRefreshLayout.OnRefreshListener {



    String urlHome;
    String token;
    String urlInfo;
    String urlStart;
    String url;
    String urlAction;
    String urlUpdateExtFolder;

    ArrayList<String> urlStack;
    String thisWhat = "/";
    ArrayList<String> thisWhatStack;

    String whatList;

    boolean isCopy = false;

    SharedPreferences sp;

    ListView list;
    ArrayList<DataModel> adapter;

    ArrayList<String> aryNumTo;
    ArrayList<String> aryNumFrom;
    ArrayList<String> aryNumTrans;
    ArrayList<String> aryExt;
    ArrayList<String> aryCallDur;
    ArrayList<String> aryCallId;

    ActionMode actMode;

    SwipeRefreshLayout swprl;

    MaterialAlertDialogBuilder dialog;
    EditText edtDialog;
    String titleApp;

    Menu menu;
    boolean onBack;
    private ArrayList<String> aryImage;

    public sipCallsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sip_calls, container, false);


        swprl = v.findViewById(R.id.swipeRefresh12);
        swprl.setOnRefreshListener(this);
        swprl.setRefreshing(true);

        sp = getActivity().getSharedPreferences("User", 0);
        token = sp.getString("token", "");


        urlHome = "https://www.call2all.co.il/ym/api/GetSipAccountsInCustomer?token=" + token;

        url = urlHome;
        list = v.findViewById(R.id.list111112);
        new sendApiRequest(getActivity(), this, "url", url);

        return v;
    }

    @Override
    public void onSuccess(String result, String type) {

    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {

    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        new sendApiRequest(getActivity(), this, "url", url);
    }
}