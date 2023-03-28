package com.mordechay.yemotapp.ui.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.securingListOnItemActionClickListener;
import com.mordechay.yemotapp.network.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;
import com.mordechay.yemotapp.ui.programmatically.list_for_securing_login_log.SecuringSessionItem;
import com.mordechay.yemotapp.ui.programmatically.list_for_securing_login_log.SessionListCustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class securingFragment extends Fragment implements OnRespondsYmtListener, View.OnClickListener, securingListOnItemActionClickListener {

    private int validationCalls;
    private LinearLayout lnrVerify;
    private LinearLayout lnrBody;
    private Button btnVerify;
    private LinearLayout digLnrVerify;
    private LinearLayout digLnrProgress;
    private ArrayList<SecuringSessionItem> arySecuringSessionItems;
    private RecyclerView recyclerView;
    private EditText digEdtVerify;
    private Button digBtnVerify;
    private AlertDialog dialogDoubleAuth;
    private final String[] tabsText = {"one", "two", "three"};

    public securingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_securing, container, false);


        lnrVerify = v.findViewById(R.id.lnrVerify);
        lnrBody = v.findViewById(R.id.lnrBody);

        btnVerify = v.findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(this);


        new SendRequestForYemotServer(requireActivity(), this, "token_information" , Constants.URL_SECURING_GET_TOKEN_INFORMATION + DataTransfer.getToken());
        return v;
    }

    @Override
    public void onSuccess(String result, String type) {
        JSONObject jsonObject = null;
        try {
        if(type.equals("token_information")){
            JSONObject jsonObjectResult = new JSONObject(result);
            jsonObject = jsonObjectResult.getJSONObject("tokenData");
            validationCalls = jsonObject.getInt("validationCalls");
            setDoubleAuth(jsonObject.getBoolean("doubleAuthStatus"));
        } else if (type.equals("double_auth_one_step")) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_securing_double_auth, null);
            MaterialAlertDialogBuilder digSendSMSBuilder = new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("אימות דו שלבי")
                    .setMessage("אנא הקישו בשדה קוד את 4 הספרות האחרונות של מספר הטלפון ממנו קיבלתם שיחה כעת:")
                    .setView(v);
            digLnrVerify = v.findViewById(R.id.lnr_securing_double_auth_verify);
            digLnrProgress = v.findViewById(R.id.lnr_securing_double_auth_progress);
            digEdtVerify = v.findViewById(R.id.edt_dialog_securing_double_auth_verify_code);
            digBtnVerify = v.findViewById(R.id.btn_dialog_securing_double_auth_verify);
            digBtnVerify.setOnClickListener(this);
            dialogDoubleAuth = digSendSMSBuilder.create();
            dialogDoubleAuth.show();
        } else if (type.equals("double_auth_two_step")) {
                jsonObject = new JSONObject(result);
                if(jsonObject.getString("responseStatus").equalsIgnoreCase("OK") && jsonObject.getString("message").equalsIgnoreCase("VerifiedOK")){
                    digLnrProgress.setVisibility(View.GONE);
                    dialogDoubleAuth.setMessage("האימות בוצע בהצלחה");
                    setDoubleAuth(true);
                }else {
                    digLnrProgress.setVisibility(View.GONE);
                    dialogDoubleAuth.setMessage("האימות נכשל");
                }
        }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {

    }

    @Override
    public void onClick(View view) {
        if(view == btnVerify){
                new SendRequestForYemotServer(requireActivity(), this, "double_auth_one_step", Constants.URL_SECURING_DOUBLE_AUTH + DataTransfer.getToken() + "&action=SendCode");
        } else if (view == digBtnVerify) {
            digLnrVerify.setVisibility(View.GONE);
            dialogDoubleAuth.setMessage("");
            digLnrProgress.setVisibility(View.VISIBLE);
            String code = digEdtVerify.getText().toString();
            new SendRequestForYemotServer(requireActivity(), this, "double_auth_two_step", Constants.URL_SECURING_DOUBLE_AUTH + DataTransfer.getToken() + "&action=VerifyCode&code=" + code);
        }
    }

    private void startGetSessionList(){

        new SendRequestForYemotServer(requireActivity(), this, "get_sessions", Constants.URL_SECURING_GET_SESSION + DataTransfer.getToken());
    }



    private void setDoubleAuth(boolean isDoubleAuthStatus){
        if (isDoubleAuthStatus) {
            lnrVerify.setVisibility(View.GONE);
            lnrBody.setVisibility(View.VISIBLE);
        } else {
            lnrBody.setVisibility(View.GONE);
            lnrVerify.setVisibility(View.VISIBLE);
        }
    }
    private void getResponseListSession(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("responseStatus");
            if (status.equals("OK")) {
                createdSessionList(result);
            } else if (status.equals("EXCEPTION")) {
                verify();
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void createdSessionList(String result) {
        try {
            arySecuringSessionItems = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(result);
            JSONArray arySessions = jsonObject.getJSONArray("sessions");
            for (int i = 0; i < arySessions.length(); i++) {
                JSONObject objSession = arySessions.getJSONObject(i);
                String id = String.valueOf(objSession.getInt("id"));
                String token = objSession.getString("token");
                String active;

                if (objSession.getBoolean("active")){
                    active = "כן";
                } else {
                    active = "לא";
                }
                String selectedDID = objSession.getString("selectedDID");
                String remoteIP = objSession.getString("remoteIP");
                String sessionType = objSession.getString("sessionType");
                String createTime = objSession.getString("createTime");
                String lastRequest = objSession.getString("lastRequest");
                String doubleAuthStatus;
                if (objSession.getBoolean("doubleAuthStatus")){
                    doubleAuthStatus = "כן";
                } else {
                    doubleAuthStatus = "לא";
                }
                arySecuringSessionItems.add(new SecuringSessionItem(id, token, active, selectedDID, remoteIP, sessionType, createTime, lastRequest, doubleAuthStatus));
            }

            SessionListCustomAdapter myca = new SessionListCustomAdapter(arySecuringSessionItems);
            myca.setOnItemActionClickListener(this);
            recyclerView.setAdapter(myca);

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void verify() {
    }

    @Override
    public void onItemActionClick(int position) {

    }
}