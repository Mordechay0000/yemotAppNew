package com.mordechay.yemotapp.ui.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.sipListOnItemActionClickListener;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.list_for_sip_accounts.sipCustomAdapter;
import com.mordechay.yemotapp.ui.programmatically.list_for_sip_accounts.sipItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class sipCallsFragment extends Fragment implements sendApiRequest.RespondsListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, sipListOnItemActionClickListener {


    private final String url = Constants.URL_SIP_GET_ACCOUNTS + DataTransfer.getToken();

    private SwipeRefreshLayout swprl;

    private Button btnNewAccount;

    private RecyclerView recyclerView;

    private ArrayList<sipItem> sipItems;

    private AlertDialog digChangeCallerId;
    private int accountsNumber;
    private LinearLayout lnrError_sip;
    private LinearLayout lnrChangeCallerId;
    private LinearLayout lnrProgress;
    private EditText edtCallerId;
    private Button btnSave;

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

        btnNewAccount = v.findViewById(R.id.btn_sip_new_accounts);

        lnrError_sip = v.findViewById(R.id.lnr_error_sip);

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.e("testing", DataTransfer.getToken() + " and " +Constants.URL_SIP_GET_ACCOUNTS+ DataTransfer.getToken());

        new sendApiRequest(getActivity(), this, "url", url);

        return v;
    }

    @Override
    public void onSuccess(String result, String type) {
        if (type.equals("url")) {
            sipItems = new ArrayList<>();
            try {
                JSONObject jsb = new JSONObject(result);
                if (!jsb.getString("responseStatus").equalsIgnoreCase("OK")) {
                    btnNewAccount.setVisibility(View.GONE);
                    lnrError_sip.setVisibility(View.VISIBLE);
                } else {
                    lnrError_sip.setVisibility(View.GONE);
                    JSONArray jsa = jsb.getJSONArray("accounts");
                    int accountLimit = jsb.getInt("accountLimit");
                    if (accountLimit <= jsa.length()) {
                        btnNewAccount.setVisibility(View.GONE);
                    }else {
                        btnNewAccount.setVisibility(View.VISIBLE);
                    }
                    if (jsa.length() != 0) {
                        btnNewAccount.setText("צור חשבון נוסף");
                    } else if (jsa.length() == 0) {
                        btnNewAccount.setText("צור חשבון ראשון");
                    }
                    btnNewAccount.setOnClickListener(this);
                    for (int i = 0; i < jsa.length(); i++) {
                        JSONObject jsb2 = jsa.getJSONObject(i);
                        String customerExtension = jsb2.getString("customerExtension");
                        if (customerExtension.equals("null") || customerExtension.isEmpty()) {
                            customerExtension = "לא מוגדר";
                        }
                        String transport = jsb2.getString("transport");
                        transport = transport.substring(transport.length() - 3);

                        String callerId = jsb2.getString("callerid");

                        String specialCallerID = jsb2.getString("specialCallerID");
                        if (specialCallerID.isEmpty() || specialCallerID.equals("null")) {
                            specialCallerID = callerId;
                        }
                        sipItems.add(new sipItem(jsb2.getString("accountNumber"), jsb2.getString("id"), jsb2.getString("password"), customerExtension, transport, jsb2.getString("created_date"), callerId, specialCallerID));
                    }

                    sipCustomAdapter myca = new sipCustomAdapter(sipItems);
                    myca.setOnItemActionClickListener(this);
                    recyclerView.setAdapter(myca);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            swprl.setRefreshing(false);
        }else if (type.equals("new_account")){
            btnNewAccount.setEnabled(true);
            refresh();
        }else if(type.equals("udp_to_wss") || type.equals("wss_to_udp") || type.equals("remove_accounts")){
            refresh();
        }
        else if(type.equals("change_caller_id")){
            digChangeCallerId.dismiss();
            refresh();
        }
    }
            @Override
    public void onFailure(int responseCode, String responseMessage) {
swprl.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        swprl.setRefreshing(true);
        new sendApiRequest(getActivity(), this, "url", url);
    }

    @Override
    public void onClick(View view) {
        if (view == btnNewAccount){
            newAccount();
        }else if(view == btnSave){
            lnrChangeCallerId.setVisibility(View.GONE);
            lnrProgress.setVisibility(View.VISIBLE);
            String callerId = edtCallerId.getText().toString();
            if(callerId.isEmpty()){
                Toast.makeText(getActivity(), "יש להזין זיהוי יוצא", Toast.LENGTH_SHORT).show();
            }else{
                new sendApiRequest(getActivity(), this, "change_caller_id", Constants.URL_SIP_CHANGE_CALLER_ID + DataTransfer.getToken() + "&accountNumber=" + accountsNumber + "&callerId=" + callerId);
            }
        }
    }

    private void newAccount() {
        btnNewAccount.setEnabled(false);
        new sendApiRequest(getActivity(), this, "new_account", Constants.URL_SIP_NEW_ACCOUNT + DataTransfer.getToken());
    }

    @Override
    public void onActionClick(int actionType, int position) {
        accountsNumber = Integer.parseInt(sipItems.get(position).getAccountNumber());
        switch (actionType){
            case 0:
                if(sipItems.get(position).getProtocol().equalsIgnoreCase("udp")){
                    new sendApiRequest(getActivity(), this, "udp_to_wss", Constants.URL_SIP_PROTOCOL_TO_WSS + DataTransfer.getToken() + "&accountNumber=" + accountsNumber);
                }else{ //== wss
                    new sendApiRequest(getActivity(), this, "wss_to_udp", Constants.URL_SIP_PROTOCOL_TO_UDP + DataTransfer.getToken() + "&accountNumber=" + accountsNumber);
                }
                break;
            case 1:
                View v = getLayoutInflater().inflate(R.layout.dialog_change_caller_id, null);
                edtCallerId = v.findViewById(R.id.edt_dialog_caller_id);
                btnSave = v.findViewById(R.id.btn_dialog_change_caller_id);
                btnSave.setOnClickListener(this);
                lnrChangeCallerId = v.findViewById(R.id.lnr_dialog_change_caller_id);
                lnrProgress = v.findViewById(R.id.lnr_dialog_change_caller_id_progress);
                MaterialAlertDialogBuilder digChangeCallerIdBuilder = new MaterialAlertDialogBuilder(requireActivity())
                        .setTitle("שינוי זיהוי יוצא")
                        .setView(v);
                digChangeCallerId = digChangeCallerIdBuilder.create();
                digChangeCallerId.show();
                break;
            case 2:
                new sendApiRequest(getActivity(), this, "remove_accounts", Constants.URL_SIP_REMOVE_ACCOUNTS + DataTransfer.getToken() + "&accountNumber=" + accountsNumber);
                break;
        }
    }
}