package com.mordechay.yemotapp.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.securingListOnItemActionClickListener;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.list_for_securing_login_log.SecuringSessionItem;
import com.mordechay.yemotapp.ui.programmatically.list_for_securing_login_log.SessionListCustomAdapter;
import com.mordechay.yemotapp.ui.programmatically.list_for_sip_accounts.sipCustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class securingFragment extends Fragment implements sendApiRequest.RespondsListener, View.OnClickListener, securingListOnItemActionClickListener {

    private String token;
    private boolean isDoubleAuthStatus;
    private LinearLayout lnrVerify;
    private LinearLayout lnrBody;
    private Button btnVerify;
    private ArrayList<SecuringSessionItem> arySecuringSessionItems;
    private RecyclerView recyclerView;

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

        recyclerView = v.findViewById(R.id.recyclerViewSession);

        new sendApiRequest(requireActivity(), this, "securing_login", Constants.URL_LOGIN + "username=" + DataTransfer.getUsername() + "&password=" + DataTransfer.getInfoPassword());
        return v;
    }

    @Override
    public void onSuccess(String result, String type) {
        if (type.equals("securing_login")) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                token = jsonObject.getString("token");
                DataTransfer.setTokenSecurity(token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {

    }

    @Override
    public void onClick(View view) {

    }

    private void startGetSessionList(){
        if (isDoubleAuth()){
            new sendApiRequest(requireActivity(), this, "get_sessions", Constants.URL_SECURING_GET_SESSION + DataTransfer.getTokenSecurity());
        }else {
            Toast.makeText(requireActivity(), "יש לבצע אימות דו שלבי", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean isDoubleAuth(){
        if (isDoubleAuthStatus) {
            lnrVerify.setVisibility(View.GONE);
            lnrBody.setVisibility(View.VISIBLE);
            return true;
        } else {
            lnrBody.setVisibility(View.GONE);
            lnrVerify.setVisibility(View.VISIBLE);
            return false;
        }
    }
    private void getResponseListSession(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("responseStatus");
            if (status.equals("OK")) {
                isDoubleAuthStatus = true;
                createdSessionList(result);
            } else if (status.equals("EXCEPTION")) {
                isDoubleAuthStatus = false;
                verify();
            } else {
                isDoubleAuthStatus = false;
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