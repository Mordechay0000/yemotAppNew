package com.mordechay.yemotapp.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.data.Filter;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.Network;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class UnitsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, OnRespondsYmtListener {

    private SendRequestForYemotServer snd;
    private Filter flt;
    private Button btnTrans;
    private Button btnFilter;
    private SwipeRefreshLayout spr;

    private String url;
    private RecyclerView recyclerView;
    private EditText edtDialogToSystem;
    private EditText edtDialogAmount;
    private Button btnDialogTrans;
    private AlertDialog altDialog;
    private TextView txtFilter;
    private Button btnDialogFilter;
    private EditText edtDialogFilterFrom;
    private EditText edtDialogFilterLimit;
    private String text = "";
    private LinearLayout lnrDialogTrans = null;
    private LinearLayout lnrDialogProgress = null;
    private AlertDialog dialogBuilder;
    private CustomAdapter adapter;


    public UnitsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_units, container, false);

        flt = Filter.getInstance(requireContext().getApplicationContext());

        btnTrans = v.findViewById(R.id.button_units_transfer);
        btnFilter = v.findViewById(R.id.button_units_filter);
        btnTrans.setOnClickListener(this);
        btnFilter.setOnClickListener(this);

        spr = v.findViewById(R.id.swipe_units);
        spr.setOnRefreshListener(this);
        spr.setRefreshing(true);

        recyclerView = v.findViewById(R.id.units_recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        txtFilter = v.findViewById(R.id.txt_filter);

        url = Constants.URL_GET_UNITS_HISTORY + DataTransfer.getToken();

        snd = SendRequestForYemotServer.getInstance(getActivity(), this);
        snd.addRequestAndSend(Network.GET_UNITS_HISTORY, url);
        
        return v;
    }

    @Override
    public void onClick(View view) {
        MaterialAlertDialogBuilder dialog;

        if(view == btnTrans){
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_transfer_units,null);
            dialog = new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.transfer_units)
                    .setMessage(R.string.transferring_units_to_another_system_please_enter_the_destination_system_number_and_amount_of_units_to_transfer)
                    .setView(v);
            lnrDialogTrans = v.findViewById(R.id.lnr_dialog_transfer_units);
            lnrDialogProgress = v.findViewById(R.id.lnr_dialog_transfer_units_progress);

            edtDialogToSystem = v.findViewById(R.id.edt_transfer_to_system);
            edtDialogAmount = v.findViewById(R.id.edt_transfer_amount);
            btnDialogTrans = v.findViewById(R.id.button_transfer_units);
            btnDialogTrans.setOnClickListener(this);
            dialogBuilder = dialog.create();
            dialogBuilder.show();
        }else if(view == btnDialogTrans){
            dialogBuilder.setMessage("");
            lnrDialogTrans.setVisibility(View.GONE);
            lnrDialogProgress.setVisibility(View.VISIBLE);
            String to = edtDialogToSystem.getText().toString();
            String amount = edtDialogAmount.getText().toString();

            String urlTransferUnits = Constants.URL_TRANSFER_UNITS + DataTransfer.getToken() + "&destination=" + URLEncoder.encode(to) + "&amount=" + URLEncoder.encode(amount);
            snd.addRequestAndSend(Network.TRANSFER_UNITS, urlTransferUnits);

    }else if (view == btnFilter){
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_filter_units,null);
            dialog = new MaterialAlertDialogBuilder(getActivity())
                    .setTitle(R.string.filter)
                    .setView(v);
            edtDialogFilterFrom = v.findViewById(R.id.edt_dialog_from);
            edtDialogFilterLimit = v.findViewById(R.id.edt_dialog_limit);
            btnDialogFilter = v.findViewById(R.id.button_dialog_units_filter);
            btnDialogFilter.setOnClickListener(this);
            altDialog = dialog.show();
        }else if(view == btnDialogFilter){
            altDialog.dismiss();
            String from = edtDialogFilterFrom.getText().toString();
            String limit = edtDialogFilterLimit.getText().toString();

            String body = "";
            text = "";
            if(!from.isEmpty()){
                try {
                    body +=  "&from=" + URLEncoder.encode(from, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                text +=  getString(R.string.showing_results_from) + " " + from +" ";
            }
            if (!limit.isEmpty()){
                try {
                    body += "&limit=" + URLEncoder.encode(limit, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                text +=  getString(R.string.number_of_results_limited_to) + " " + limit;
            }

            if (from.isEmpty() && limit.isEmpty()){
                text =  getString(R.string.all);
            }

            txtFilter.setText(text);
            url = Constants.URL_GET_UNITS_HISTORY + DataTransfer.getToken() + body;
            refresh();
        }
    }



    private void refresh(){
        spr.setRefreshing(true);
        snd.addRequestAndSend(Network.GET_UNITS_HISTORY, url);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onSuccess(String result, int type) {

        if (type == Network.GET_UNITS_HISTORY) {

            adapter = new CustomAdapter(null, R.layout.item_units, new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7});
            try {
                JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.isNull("transactions")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("transactions");
                            for (int i = 1; i <= jsonArray.length(); i++) {
                                Drawable image = flt.getTypeImage("wav");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i - 1);

                                String id = "";
                                if (!jsonObject1.isNull("id")) {
                                    id = String.valueOf(jsonObject1.getInt("id"));
                                }
                                String transactionTime = "";
                                if (!jsonObject1.isNull("transactionTime")) {
                                    transactionTime = jsonObject1.getString("transactionTime");
                                }
                                String amount = "";
                                if (!jsonObject1.isNull("amount")) {
                                    amount = String.valueOf(jsonObject1.getDouble("amount"));
                                }
                                String description = "";
                                if (!jsonObject1.isNull("description")) {
                                    description = jsonObject1.getString("description");
                                    if(description.equals("Start"))
                                        description = getString(R.string.billing_for_running_a_campaign);
                                    else if(description.equals("transfer to"))
                                        description = getString(R.string.charge_for_transferring_units_to_another_system);
                                    else if(description.equals("transfer from"))
                                        description = getString(R.string.credit_for_transferring_units_from_another_system);
                                    else if(description.equals("Units expired"))
                                        description = getString(R.string.units_have_expired);
                                }
                                String who = "";
                                if (!jsonObject1.isNull("who")) {
                                    who = jsonObject1.getString("who");
                                }
                                String newBalance = "";
                                if (!jsonObject1.isNull("newBalance")) {
                                    newBalance = String.valueOf(jsonObject1.getDouble("newBalance"));
                                }
                                String expireDate = "";
                                if (!jsonObject1.isNull("expireDate")) {
                                    expireDate = jsonObject1.getString("expireDate");
                                }
                                String campaignId = "";
                                if (!jsonObject1.isNull("campaignId")) {
                                    campaignId = String.valueOf(jsonObject1.getInt("campaignId"));
                                }
                                adapter.addItem(image, new String[]{transactionTime, amount, description, who, newBalance, expireDate, campaignId}, new String[]{id});
                            }


                        recyclerView.setAdapter(adapter);
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            spr.setRefreshing(false);
        }else if(type == Network.TRANSFER_UNITS){
            try {
                JSONObject jsb = new JSONObject(result);
                lnrDialogProgress.setVisibility(View.GONE);
                dialogBuilder.setTitle(R.string.conclusion);

                String rspStatus = jsb.getString("responseStatus");
                String out;
                if (rspStatus.equals("OK")) {
                    out =  getString(R.string.successfully_transferred) + " "  +   jsb.getString("amount") + " " + getString(R.string.units_to_the_system) + " " + jsb.getString("destination") + "\n \n " +  getString(R.string.updated_unit_balance)  + " " + jsb.getString("newBalance");
                } else {
                    String ErrorMessage = jsb.getString("message");
                    if(ErrorMessage.equalsIgnoreCase("Bad_destination"))
                        ErrorMessage = getString(R.string.the_system_does_not_exist_or_is_not_authorized_to_receive_units_from_this_system);
                    else if(ErrorMessage.equalsIgnoreCase("Bad_amount"))
                        ErrorMessage = getString(R.string.the_amount_of_units_to_transfer_is_invalid);
                    else if(ErrorMessage.equalsIgnoreCase("Not_enough balance"))
                        ErrorMessage = getString(R.string.there_are_not_enough_units_in_the_system);

                    out = getString(R.string.failed_to_transfer_units) + "\n "+ getString(R.string.cause) +" \n" + ErrorMessage;
                }
                dialogBuilder.setMessage(out);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            refresh();
        }

    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {
        spr.setRefreshing(false);
Log.e("error", String.valueOf(responseCode) + responseMessage);
    }
}