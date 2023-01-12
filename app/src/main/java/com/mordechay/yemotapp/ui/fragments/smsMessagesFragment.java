package com.mordechay.yemotapp.ui.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;
import com.mordechay.yemotapp.ui.programmatically.list.DataModel;
import com.mordechay.yemotapp.ui.programmatically.list.newList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;


public class smsMessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, sendApiRequest.RespondsListener, View.OnClickListener {









    String urlHome;
    String token;
    String urlInfo;
    String urlStart;
    String url;

    ArrayList<String> urlStack;
    String thisWhat = "/";
    ArrayList<String> thisWhatStack;

    String whatList;

    boolean isCopy = false;

    ListView list;
    ArrayList<DataModel> adapter;

    ArrayList<String> aryImage;

    Button btn;

    SwipeRefreshLayout swprl;

    MaterialAlertDialogBuilder dialog;
    AlertDialog altDialog;
    EditText edtDialog;


    Menu menu;
    boolean onBack;
    private MaterialAlertDialogBuilder rnmDialog;
    private EditText edtRenameDialog;
    private String renameWhatString;
    private ArrayList<Integer> renameWhatList;


private EditText edtFrom;
    private EditText edtMessage;
    private SwitchMaterial swmFlash;
    private EditText edtPhones;
    private Button btnSend;


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


        btn = v.findViewById(R.id.button5);
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
                    CustomAdapter csta = new CustomAdapter(this.getContext(), new newList().getAdapter(getActivity(), aryy));
                    list.setAdapter(csta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                Log.e("error parse json", e.getMessage());
            }
            swprl.setRefreshing(false);
        }else if(type.equals("send_sms")){
            try {
                JSONObject jsb = new JSONObject(result);
                dialog = new MaterialAlertDialogBuilder(getActivity());
                dialog.setTitle("סיכום");

                String rspStatus = jsb.getString("responseStatus");
                if(rspStatus.equals("OK")){
                    rspStatus = "הצלחה";
                }else if (rspStatus.equals("ERROR")){
                    rspStatus = "שגיאה";
                }

                try {
                    String strMessage = jsb.getString("message");
                    String strFrom = jsb.getString("from");
                    String strSendCount = String.valueOf(jsb.getInt("sendCount"));
                    String strBilling = String.valueOf(jsb.getInt("Billing"));

                    dialog.setMessage(
                            "סטטוס הפעולה: " + rspStatus + "\n" + "\n" +
                                    "תוכן ההודעה: " + "\n" +
                                    strMessage + "\n" + "\n" +
                                    "הזיהוי ממנו יצאה ההודעה: " + strFrom + "\n" +
                                    "כמה הודעות נשלחו: " + strSendCount + "\n" +
                                    "תשלום: " + strBilling + "\n");
                    dialog.setPositiveButton("אישור", null);
                }catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error parse json", e.getMessage());

                    dialog.setMessage("שגיאה: " + "\n" + "\n" +
                            "סטטוס הפעולה: " + rspStatus + "\n" +
                            "סיבה:" + "\n" + "\n" + jsb.getString("message") + "\n" + "\n" +
                            "ההודעה לא נשלחה");

                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.setMessage("שגיאה בניתוח תגובת השרת.");
            }
            dialog.setPositiveButton("אישור", null);
            dialog.show();
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
            dialog = new MaterialAlertDialogBuilder(getActivity())
                    .setTitle("שליחת סמס")
                    .setView(v)
            ;
            edtFrom = v.findViewById(R.id.editTextNumber100);
            edtMessage = v.findViewById(R.id.editTextNumber22);
            swmFlash = v.findViewById(R.id.switch1);
            edtPhones = v.findViewById(R.id.editTextNumber222);
            btnSend = v.findViewById(R.id.button_send_sms);
            btnSend.setOnClickListener(this);
            altDialog = dialog.show();
        }else if(view == btnSend){
            altDialog.dismiss();
            String from = edtFrom.getText().toString();
            String message = edtMessage.getText().toString();
            boolean flash = swmFlash.isChecked();
            String phones = edtPhones.getText().toString();

            String urlSendSMS = Constants.URL_SEND_SMS+ DataTransfer.getToken() + "&from=" + URLEncoder.encode(from) + "&message=" + URLEncoder.encode(message) + "&sendFlashMessage=" + flash + "&phones=" + URLEncoder.encode(phones);
            new sendApiRequest(getActivity(), this, "send_sms", urlSendSMS);
        }
    }
}