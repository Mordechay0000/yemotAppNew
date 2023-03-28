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
import com.mordechay.yemotapp.network.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class smsMessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnRespondsYmtListener, View.OnClickListener {

    String urlHome;
    String token;
    String url;


    RecyclerView recyclerView;
    CustomAdapter adapter;

    FloatingActionButton btn;

    SwipeRefreshLayout swprl;

    AlertDialog digSendSMS;

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

        new SendRequestForYemotServer(getActivity(), this, "url", url);

        return v;
    }


    public void refresh() {
        swprl.setRefreshing(true);
        new SendRequestForYemotServer(getActivity(), this, "url", url);
    }


    @Override
    public void onRefresh() {
refresh();
    }











    @Override
    public void onSuccess(String result, String type) {
        if (type.equals("url")) {
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
                            strForIsDeliveryText = "לא התקבל מידע על מסירת ההודעה";
                            break;
                        case "DELIVRD":
                            strForIsDeliveryText = "נמסר";
                            break;
                        case "ESME_ROK":
                            strForIsDeliveryText = "הועבר לשליחה בהצלחה";
                            break;
                        case "EXPIRED":
                            strForIsDeliveryText = "פג תוקף מעקב המסירה";
                            break;
                        case "ESME_RINVDSTADR":
                            strForIsDeliveryText = "יעד לא נגיש";
                            break;
                        case "ESME_RINVMSGLEN":
                            strForIsDeliveryText = "אורך לא חוקי";
                            break;
                        case "ESME_RINVCMDLEN":
                            strForIsDeliveryText = "אורך פקודה שגוי";
                            break;
                        case "ESME_RMSGQFUL":
                            strForIsDeliveryText = "תור ההודעות מלא";
                            break;
                        case "ESME_RINVNUMDESTS":
                            strForIsDeliveryText = "מספר יעדים לא חוקי";
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
        }else if(type.equals("send_sms")){
            lnrProgress.setVisibility(View.GONE);
            digSendSMS.setTitle("סיכום");

            try {
                JSONObject jsb = new JSONObject(result);

                String rspStatus = jsb.getString("responseStatus");
                if(rspStatus.equals("OK")){
                    rspStatus = "הצלחה";
                }else if (rspStatus.equals("ERROR")){
                    rspStatus = "שגיאה";
                }

                try {
                    String strMessage = jsb.getString("message");
                    if(strMessage.equalsIgnoreCase("Low unit balance")){
                        strMessage = "אין מספיק יחידות";
                    }
                    String strFrom = jsb.getString("from");
                    String strSendCount = String.valueOf(jsb.getInt("sendCount"));
                    String strBilling = String.valueOf(jsb.getInt("Billing"));

                    digSendSMS.setMessage(
                            "סטטוס הפעולה: " + rspStatus + "\n" + "\n" +
                                    "תוכן ההודעה: " + "\n" +
                                    strMessage + "\n" + "\n" +
                                    "הזיהוי ממנו יצאה ההודעה: " + strFrom + "\n" +
                                    "כמה הודעות נשלחו: " + strSendCount + "\n" +
                                    "תשלום: " + strBilling + "\n");
                }catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error parse json", e.getMessage());

                    digSendSMS.setMessage("שגיאה: " + "\n" + "\n" +
                            "סטטוס הפעולה: " + rspStatus + "\n" +
                            "סיבה:" + "\n" + "\n" + jsb.getString("message") + "\n" + "\n" +
                            "ההודעה לא נשלחה");

                }
            } catch (JSONException e) {
                e.printStackTrace();
                digSendSMS.setMessage("שגיאה בניתוח תגובת השרת.");
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
                    .setTitle("שליחת סמס")
                    .setMessage("טופס לשליחת סמס")
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
            new SendRequestForYemotServer(getActivity(), this, "send_sms", urlSendSMS);
        }
    }
}