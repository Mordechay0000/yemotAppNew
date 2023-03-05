package com.mordechay.yemotapp.ui.fragments;

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
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;

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

        new sendApiRequest(getActivity(), this, "getUnitsHistory", url);
        
        return v;
    }

    @Override
    public void onClick(View view) {
        MaterialAlertDialogBuilder dialog;

        if(view == btnTrans){
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_transfer_units,null);
            dialog = new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("העברת יחידות")
                    .setMessage("העברת יחידות למערכת אחרת: \n אנא הזן מספר מערכת היעד וכמות יחידות להעברה")
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
            url = Constants.URL_GET_UNITS_HISTORY + DataTransfer.getToken() + body;
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

            adapter = new CustomAdapter(null, R.layout.item_units, new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7});
            try {
                JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.isNull("transactions")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("transactions");
                            for (int i = 1; i <= jsonArray.length(); i++) {
                                String image = String.valueOf(R.drawable.ic_baseline_audio_file_24);
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
                                        description = "חיוב עבור הפעלת קמפיין";
                                    else if(description.equals("transfer to"))
                                        description = "חיוב עבור העברת יחידות למערכת אחרת";
                                    else if(description.equals("transfer from"))
                                        description = "זיכוי עבור העברת יחידות ממערכת אחרת";
                                    else if(description.equals("Units expired"))
                                        description = "פג תוקף היחידות";
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
                                adapter.addItem(Integer.parseInt(image), new String[]{transactionTime, amount, description, who, newBalance, expireDate, campaignId}, new String[]{id});
                            }


                        recyclerView.setAdapter(adapter);
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            spr.setRefreshing(false);
        }else if(type.equals("transfer_units")){
            try {
                JSONObject jsb = new JSONObject(result);
                lnrDialogProgress.setVisibility(View.GONE);
                dialogBuilder.setTitle("סיכום");

                String rspStatus = jsb.getString("responseStatus");
                String out;
                if (rspStatus.equals("OK")) {
                    out =  "הועברו בהצלחה "   +   jsb.getString("amount") + " יחידות למערכת " + jsb.getString("destination") + "\n \n יתרת יחידות מעודכנת: " + jsb.getString("newBalance");
                } else {
                    String ErrorMessage = jsb.getString("message");
                    if(ErrorMessage.equalsIgnoreCase("Bad_destination"))
                        ErrorMessage = "המערכת אינה קיימת או שאינה מורשית לקבל יחידות ממערכת זו";
                    else if(ErrorMessage.equalsIgnoreCase("Bad_amount"))
                        ErrorMessage = "סכום היחידות להעברה אינו חוקי";
                    else if(ErrorMessage.equalsIgnoreCase("Not_enough balance"))
                        ErrorMessage = "אין יחידות מספיקות במערכת";

                    out = "העברת יחידות נכשלה" + "\n סיבה: \n" + ErrorMessage;
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