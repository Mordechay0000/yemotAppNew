package com.mordechay.yemotapp.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.Network;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class smsMessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnRespondsYmtListener, View.OnClickListener {

    private SendRequestForYemotServer snd;
    private String urlHome;
    private String token;
    private String url;


    private RecyclerView recyclerView;
    private CustomAdapter adapter;

    private FloatingActionButton btn;

    private SwipeRefreshLayout swprl;

    private AlertDialog digSendSMS;

private EditText edtFrom;
    private EditText edtMessage;
    private SwitchMaterial swmFlash;
    private EditText edtPhones;
    private Button btnSend;
    private LinearLayout lnrSendSMS;
    private LinearLayout lnrProgress;


    public smsMessagesFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sms_messages, container, false);


        token = DataTransfer.getToken();


        swprl = v.findViewById(R.id.swipeRefresh_sms);
        swprl.setOnRefreshListener(this);
        swprl.setRefreshing(true);


        btn = v.findViewById(R.id.sms_writing_sms_message);
        btn.setOnClickListener(this);


        urlHome = "https://www.call2all.co.il/ym/api/GetSmsOutLog?token=" + token;
        url = urlHome;


        recyclerView = v.findViewById(R.id.sms_messages_recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        snd = SendRequestForYemotServer.getInstance(getActivity(), this);
        snd.addRequestAndSend(Network.GET_SMS_HISTORY, url);

        return v;
    }


    public void refresh() {
        swprl.setRefreshing(true);
        snd.addRequestAndSend(Network.GET_SMS_HISTORY, url);
    }


    @Override
    public void onRefresh() {
refresh();
    }











    @Override
    public void onSuccess(String result, int type) {
        if (type == Network.GET_SMS_HISTORY) {
            adapter = new CustomAdapter(null, R.layout.item_sms_messages, new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7});
            try {
                JSONObject jsb = new JSONObject(result);
                JSONArray jsa;
                if (!jsb.isNull("rows")) {
                    jsa = jsb.getJSONArray("rows");
                } else {
                    jsa = new JSONArray();
                }

                for (int i = 0; i < jsa.length(); i++) {
                    JSONObject js = jsa.getJSONObject(i);

                    String isDelivery = js.getString("DeliveryReport");

                    int sImage;
                    if(isDelivery.equals("ESME_RINVDSTADR") || isDelivery.equals("ESME_RINVMSGLEN") || isDelivery.equals("ESME_RINVCMDLEN") || isDelivery.equals("ESME_RMSGQFUL")||isDelivery.equals("ESME_RINVNUMDESTS")) {
                        sImage = R.drawable.ic_baseline_sms_failed_24;
                    }else{
                        sImage = R.drawable.ic_baseline_sms_24;
                    }
                    Drawable image = ResourcesCompat.getDrawable(getActivity().getResources(), sImage, getActivity().getTheme());

                    String callerId = js.getString("CallerId");
                    String to = js.getString("To");
                    String message = js.getString("Message");
                    String billing = String.valueOf(js.getInt("Billing"));
                    String runBy = js.getString("RunBy");
                    String time = js.getString("Time");

                    String strForIsDeliveryText;
                    switch (isDelivery) {
                        case "null":
                            strForIsDeliveryText = getString(R.string.no_information_was_received_about_the_delivery_of_the_message);
                            break;
                        case "DELIVRD":
                            strForIsDeliveryText = getString(R.string.delivered);
                            break;
                        case "ESME_ROK":
                            strForIsDeliveryText = getString(R.string.successfully_sent);
                            break;
                        case "EXPIRED":
                            strForIsDeliveryText = getString(R.string.delivery_tracking_has_expired);
                            break;
                        case "ESME_RINVDSTADR":
                            strForIsDeliveryText = getString(R.string.unreachable_destination);
                            break;
                        case "ESME_RINVMSGLEN":
                            strForIsDeliveryText = getString(R.string.invalid_length);
                            break;
                        case "ESME_RINVCMDLEN":
                            strForIsDeliveryText = getString(R.string.invalid_command_length);
                            break;
                        case "ESME_RMSGQFUL":
                            strForIsDeliveryText = getString(R.string.the_message_queue_is_full);
                            break;
                        case "ESME_RINVNUMDESTS":
                            strForIsDeliveryText = getString(R.string.invalid_destination_number);
                            break;
                        default:
                            strForIsDeliveryText = isDelivery;
                    }

                    adapter.addItem(image, new String[]{callerId, to, message, billing, runBy, time, strForIsDeliveryText});
                }
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                Log.e("error parse json", e.getMessage());
            }
            swprl.setRefreshing(false);
        }else if(type == Network.SEND_SMS){
            lnrProgress.setVisibility(View.GONE);
            digSendSMS.setTitle(getString(R.string.conclusion));

            try {
                JSONObject jsb = new JSONObject(result);

                String rspStatus = jsb.getString("responseStatus");
                if(rspStatus.equals("OK")){
                    rspStatus = getString(R.string.success);
                }else if (rspStatus.equals("ERROR")){
                    rspStatus = getString(R.string.error);
                }

                try {
                    String strMessage = jsb.getString("message");
                    if(strMessage.equalsIgnoreCase("Low unit balance")){
                        strMessage = getString(R.string.not_enough_units);
                    }
                    String strFrom = jsb.getString("from");
                    String strSendCount = String.valueOf(jsb.getInt("sendCount"));
                    String strBilling = String.valueOf(jsb.getInt("Billing"));

                    digSendSMS.setMessage(
                            getString(R.string.action_status) + " " + rspStatus + "\n" + "\n" +
                                    getString(R.string.content_of_the_message_with_colon)+ " " + "\n" +
                                    strMessage + "\n" + "\n" +
                                    getString(R.string.the_id_from_which_the_message_originated) + " " + strFrom + "\n" +
                                    getString(R.string.how_many_messages_have_been_sent) + " " + strSendCount + "\n" +
                                    getString(R.string.payment) + " " + strBilling + "\n");
                }catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error parse json", e.getMessage());

                    digSendSMS.setMessage(getString(R.string.error_with_colon) + " " + "\n" + "\n" +
                            getString(R.string.action_status) + " " + rspStatus + "\n" +
                            getString(R.string.cause) + " " + "\n" + "\n" + jsb.getString("message") + "\n" + "\n" +
                            getString(R.string.the_message_was_not_sent));

                }
            } catch (JSONException e) {
                e.printStackTrace();
                digSendSMS.setMessage(getString(R.string.error_parsing_server_response));
            }
            digSendSMS.show();
        }
    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {
        swprl.setRefreshing(false);
    }

    @Override
    public void onClick(View view) {



        if(view == btn){
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_send_sms,null);
            MaterialAlertDialogBuilder digSendSMSBuilder = new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.sending_sms)
                    .setMessage(R.string.sms_sending_form)
                    .setView(v);
            edtFrom = v.findViewById(R.id.editTextNumber100);
            edtMessage = v.findViewById(R.id.editTextNumber22);
            swmFlash = v.findViewById(R.id.switch1);
            edtPhones = v.findViewById(R.id.editTextNumber222);
            btnSend = v.findViewById(R.id.button_send_sms);
            btnSend.setOnClickListener(this);
            lnrSendSMS = v.findViewById(R.id.lnr_dialog_send_sms);
            lnrProgress = v.findViewById(R.id.lnr_dialog_send_sms_progress);
            digSendSMS = digSendSMSBuilder.create();
            digSendSMS.show();
        }else if(view == btnSend){
            lnrSendSMS.setVisibility(View.GONE);
            lnrProgress.setVisibility(View.VISIBLE);
            digSendSMS.setMessage("");
            String from = edtFrom.getText().toString();
            String message = edtMessage.getText().toString();
            boolean flash = swmFlash.isChecked();
            String phones = edtPhones.getText().toString();

            String urlSendSMS = null;
            try {
                urlSendSMS = Constants.URL_SEND_SMS+ DataTransfer.getToken() + "&from=" + URLEncoder.encode(from, "UTF-8") + "&message=" + URLEncoder.encode(message, "UTF-8") + "&sendFlashMessage=" + flash + "&phones=" + URLEncoder.encode(phones, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            snd.addRequestAndSend(Network.SEND_SMS, urlSendSMS);
        }
    }
}