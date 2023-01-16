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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;
import com.mordechay.yemotapp.ui.programmatically.list.newList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;


public class smsMessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, sendApiRequest.RespondsListener, View.OnClickListener {

    String urlHome;
    String token;
    String url;


    ListView list;

    ArrayList<String> aryImage;

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


        list = v.findViewById(R.id.list1111);

        new sendApiRequest(getActivity(), this, "url", url);

        return v;
    }


    public void refresh() {
        swprl.setRefreshing(true);
        new sendApiRequest(getActivity(), this, "url", url);
    }


    @Override
    public void onRefresh() {
refresh();
    }











    @Override
    public void onSuccess(String result, String type) {
        if (type.equals("url")) {
            try {
                JSONObject jsb = new JSONObject(result);
                JSONArray jsa;
                if (!jsb.isNull("rows")) {
                    jsa = jsb.getJSONArray("rows");
                } else {
                    jsa = new JSONArray();
                }


                aryImage = new ArrayList<>();
                ArrayList CallerId = new ArrayList();
                ArrayList To	= new ArrayList();
                ArrayList Message= new ArrayList();
                ArrayList Billing= new ArrayList();
                ArrayList RunBy= new ArrayList();
                ArrayList Time= new ArrayList();
                ArrayList DeliveryReport= new ArrayList();


                for (int i = 0; i < jsa.length(); i++) {
                    JSONObject js = jsa.getJSONObject(i);

                    String isDelivery = js.getString("DeliveryReport");

                    String image;
                    if(isDelivery.equals("ESME_RINVDSTADR") || isDelivery.equals("ESME_RINVMSGLEN") || isDelivery.equals("ESME_RINVCMDLEN") || isDelivery.equals("ESME_RMSGQFUL")||isDelivery.equals("ESME_RINVNUMDESTS")) {
                        image = String.valueOf(R.drawable.ic_baseline_sms_failed_24);
                    }else{
                        image = String.valueOf(R.drawable.ic_baseline_sms_24);
                    }
                    aryImage.add(image);


                    CallerId.add(js.getString("CallerId"));
                    To.add(js.getString("To"));
                    Message.add(js.getString("Message"));
                    Billing.add(js.getInt("Billing"));
                    RunBy.add(js.getString("RunBy"));
                    Time.add(js.getString("Time"));

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


                    DeliveryReport.add(strForIsDeliveryText);
                }


                ArrayList<ArrayList<String>> aryy = new ArrayList<ArrayList<String>>();
                aryy.add(aryImage);
                aryy.add(CallerId);
                aryy.add(To);
                aryy.add(Message);
                aryy.add(Billing);
                aryy.add(RunBy);
                aryy.add(Time);
                aryy.add(DeliveryReport);

                try {
                    CustomAdapter csta = new CustomAdapter(requireContext(), new newList().getAdapter(getActivity(), aryy));
                    list.setAdapter(csta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public void onFailure(int responseCode, String responseMessage) {
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
            new sendApiRequest(getActivity(), this, "send_sms", urlSendSMS);
        }
    }
}