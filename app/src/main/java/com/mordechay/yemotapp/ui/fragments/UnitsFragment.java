package com.mordechay.yemotapp.ui.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;
import com.mordechay.yemotapp.ui.programmatically.list.newList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;


public class UnitsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, sendApiRequest.RespondsListener {

    private Button btnTrans;
    private Button btnFilter;
    private SwipeRefreshLayout spr;

    private String url;
    private ArrayList adapter;
    private ArrayList aryId;
    private ArrayList aryTransactionTime;
    private ArrayList aryAmount;
    private ArrayList aryDescription;
    private ArrayList aryWho;
    private ArrayList aryNewBalance;
    private ArrayList aryExpireDate;
    private ArrayList aryCampaignId;
    private ListView list;
    private EditText edtDialogToSystem;
    private EditText edtDialogAmount;
    private Button btnDialogTrans;
    private AlertDialog altDialog;
    private TextView txtFilter;
    private Button btnDialogFilter;
    private EditText edtDialogFilterFrom;
    private EditText edtDialogFilterLimit;
    private String text = "";


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
        btnTrans = v.findViewById(R.id.button_units_transfer);
        btnFilter = v.findViewById(R.id.button_units_filter);
        btnTrans.setOnClickListener(this);
        btnFilter.setOnClickListener(this);

        spr = v.findViewById(R.id.swipe_units);
        spr.setOnRefreshListener(this);
        spr.setRefreshing(true);

        list = v.findViewById(R.id.list_units);

        txtFilter = v.findViewById(R.id.txt_filter);

        url = Constants.URL_GET_UNITS_HISTORY;

        new sendApiRequest(getActivity(), this, "getUnitsHistory", url);
        
        return v;
    }

    @Override
    public void onClick(View view) {


        MaterialAlertDialogBuilder dialog;
        if(view == btnTrans){
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_transfer_units,null);
            dialog = new MaterialAlertDialogBuilder(getActivity())
                    .setTitle("העברת יחידות")
                    .setView(v);
            edtDialogToSystem = v.findViewById(R.id.edt_transfer_to_system);
            edtDialogAmount = v.findViewById(R.id.edt_transfer_amount);
            btnDialogTrans = v.findViewById(R.id.button_transfer_units);
            btnDialogTrans.setOnClickListener(this);
            altDialog = dialog.show();
        }else if(view == btnDialogTrans){
            altDialog.dismiss();
            String to = edtDialogToSystem.getText().toString();
            String amount = edtDialogAmount.getText().toString();

            String urlTransferUnits = Constants.URL_TRANSFER_UNITS + "&destination=" + URLEncoder.encode(to) + "&amount=" + URLEncoder.encode(amount);
            new sendApiRequest(getActivity(), this, "transfer_units", urlTransferUnits);

    }else if (view == btnFilter){
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_filter_units,null);
            dialog = new MaterialAlertDialogBuilder(getActivity())
                    .setTitle("סינון")
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
                body +=  "&from=" + URLEncoder.encode(from);
                text +=  "מציג תוצאות מ: " + from +" ";
            }
            if (!limit.isEmpty()){
                body += "&limit=" + URLEncoder.encode(limit);
                text +=  "מספר תוצאות מוגבל ל: " + limit;
            }

            txtFilter.setText(text);
            url = Constants.URL_GET_UNITS_HISTORY + body;
            refresh();
        }
    }



    private void refresh(){
        spr.setRefreshing(true);
        new sendApiRequest(getActivity(), this, "getUnitsHistory", url);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onSuccess(String result, String type) {

        if (type.equals("getUnitsHistory")) {

            adapter = new ArrayList();
            try {
                // TODO - remove this aryImage later change list to custom recyclerView
                ArrayList aryImage = new ArrayList();
                aryId = new ArrayList<String>();
                aryTransactionTime = new ArrayList<String>();
                aryAmount = new ArrayList<String>();
                aryDescription = new ArrayList<String>();
                aryWho = new ArrayList<String>();
                aryNewBalance = new ArrayList<String>();
                aryExpireDate = new ArrayList<String>();
                aryCampaignId = new ArrayList<String>();

                JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.isNull("transactions")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("transactions");
                            for (int i = 1; i <= jsonArray.length(); i++) {
                                aryImage.add(String.valueOf(R.drawable.ic_baseline_audio_file_24));
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i - 1);

                                Log.e("tag", String.valueOf(jsonObject1.getInt("id")));
                                if (!jsonObject1.isNull("id")) {
                                    aryId.add(jsonObject1.getInt("id"));
                                } else {
                                    aryId.add("");
                                }
                                if (!jsonObject1.isNull("transactionTime")) {
                                    aryTransactionTime.add(jsonObject1.getString("transactionTime"));
                                } else {
                                    aryTransactionTime.add("");
                                }
                                if (!jsonObject1.isNull("amount")) {
                                    aryAmount.add(jsonObject1.getDouble("amount"));
                                } else {
                                    aryAmount.add("");
                                }
                                if (!jsonObject1.isNull("description")) {
                                    String description = jsonObject1.getString("description");
                                    if(description.equals("Start"))
                                        description = "חיוב עבור הפעלת קמפיין";
                                    else if(description.equals("transfer to"))
                                        description = "חיוב עבור העברת יחידות למערכת אחרת";
                                    else if(description.equals("transfer from"))
                                        description = "זיכוי עבור העברת יחידות ממערכת אחרת";
                                    else if(description.equals("Units expired"))
                                        description = "פג תוקף היחידות";


                                    aryDescription.add(description);
                                } else {
                                    aryDescription.add("");
                                }
                                if (!jsonObject1.isNull("who")) {
                                    aryWho.add(jsonObject1.getString("who"));
                                } else {
                                    aryWho.add("");
                                }
                                if (!jsonObject1.isNull("newBalance")) {
                                    aryNewBalance.add(String.valueOf(jsonObject1.getDouble("newBalance")));
                                } else {
                                    aryNewBalance.add("");
                                }
                                if (!jsonObject1.isNull("expireDate")) {
                                    aryExpireDate.add(jsonObject1.getString("expireDate"));
                                } else {
                                    aryExpireDate.add("");
                                }
                                if (!jsonObject1.isNull("campaignId")) {
                                    aryCampaignId.add(String.valueOf(jsonObject1.getInt("campaignId")));
                                } else {
                                    aryCampaignId.add("");
                                }

                            }

                        ArrayList<ArrayList<String>> aryyyyyyy = new ArrayList<ArrayList<String>>();
                        aryyyyyyy.add(aryImage);
                        aryyyyyyy.add(aryTransactionTime);
                        aryyyyyyy.add(aryAmount);
                        aryyyyyyy.add(aryDescription);
                        aryyyyyyy.add(aryWho);
                        aryyyyyyy.add(aryNewBalance);
                        aryyyyyyy.add(aryExpireDate);
                        aryyyyyyy.add(aryCampaignId);


                        CustomAdapter csta = new CustomAdapter(this.getContext(), new newList().getAdapter(getActivity(), aryyyyyyy));
                        list.setAdapter(csta);
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            spr.setRefreshing(false);
        }else if(type.equals("transfer_units")){
            try {
                JSONObject jsb = new JSONObject(result);
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
                dialog.setTitle("סיכום");

                String rspStatus = jsb.getString("responseStatus");
                String out;
                if (rspStatus.equals("OK")) {
                    out =  "הועברו בהצלחה "   +   jsb.getString("amount") + " יחידות למערכת " + jsb.getString("destination") + "\n \n יתרת יחידות מעודכנת: " + jsb.getString("newBalance");
                } else {
                    String ErrorMessage = jsb.getString("message");
                    if(ErrorMessage.equals("Bad destination"))
                        ErrorMessage = "המערכת אינה קיימת או שאינה מורשית לקבל יחידות ממערכת זו";
                    else if(ErrorMessage.equals("Bad amount"))
                        ErrorMessage = "סכום היחידות להעברה אינו חוקי";
                    else if(ErrorMessage.equals("Not enough balance"))
                        ErrorMessage = "אין יחידות מספיקות במערכת";

out = "העברת יחידות נכשלה" + "\n סיבה: \n" + ErrorMessage;
                }
                dialog.setMessage(out);
                dialog.setPositiveButton("אישור", null);
                dialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            refresh();
        }

    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {
        spr.setRefreshing(false);
Log.e("error", String.valueOf(responseCode) + responseMessage);
    }
}