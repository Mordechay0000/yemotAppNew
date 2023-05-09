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
import com.google.android.material.tabs.TabLayout;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.securingListOnItemActionClickListener;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.Network;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;
import com.mordechay.yemotapp.ui.fragments.extExplorerFragments.ExtExplorerSystemMessagesFragment;
import com.mordechay.yemotapp.ui.fragments.securingFragments.AllSessionsFragment;
import com.mordechay.yemotapp.ui.fragments.start.fiveFragment;
import com.mordechay.yemotapp.ui.programmatically.list_for_securing_login_log.SecuringSessionItem;
import com.mordechay.yemotapp.ui.programmatically.list_for_securing_login_log.SessionListCustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


public class securingFragment extends Fragment implements OnRespondsYmtListener, View.OnClickListener, securingListOnItemActionClickListener {

    private SendRequestForYemotServer snd;
    private final Fragment[] tabsFragments = {new AllSessionsFragment(), new ExtExplorerSystemMessagesFragment(), new fiveFragment()};
    private TabLayout tabLayout;
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

        tabLayout = v.findViewById(R.id.securing_fragment_tab_layout);


        snd =  SendRequestForYemotServer.getInstance(requireActivity(), this);
        snd.addRequestAndSend(Network.GET_TOKEN_INFORMATION, Constants.URL_SECURING_GET_TOKEN_INFORMATION + DataTransfer.getToken());
        return v;
    }

    @Override
    public void onSuccess(String result, int type) {
        JSONObject jsonObject = null;
        try {
        if(type == Network.GET_TOKEN_INFORMATION){
            JSONObject jsonObjectResult = new JSONObject(result);
            jsonObject = jsonObjectResult.getJSONObject("tokenData");
            validationCalls = jsonObject.getInt("validationCalls");
            setDoubleAuth(jsonObject.getBoolean("doubleAuthStatus"));
        } else if (type == Network.DOUBLE_AUTH_ONE_STEP) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_securing_double_auth, null);
            MaterialAlertDialogBuilder digSendSMSBuilder = new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.two_step_verification)
                    .setMessage(R.string.please_enter_the_last_4_digits_of_the_phone_number_from_which_you_just_received_a_call_in_the_code_field)
                    .setView(v);
            digLnrVerify = v.findViewById(R.id.lnr_securing_double_auth_verify);
            digLnrProgress = v.findViewById(R.id.lnr_securing_double_auth_progress);
            digEdtVerify = v.findViewById(R.id.edt_dialog_securing_double_auth_verify_code);
            digBtnVerify = v.findViewById(R.id.btn_dialog_securing_double_auth_verify);
            digBtnVerify.setOnClickListener(this);
            dialogDoubleAuth = digSendSMSBuilder.create();
            dialogDoubleAuth.show();
        } else if (type == Network.DOUBLE_AUTH_TWO_STEP) {
                jsonObject = new JSONObject(result);
                if(jsonObject.getString("responseStatus").equalsIgnoreCase("OK") && jsonObject.getString("message").equalsIgnoreCase("VerifiedOK")){
                    digLnrProgress.setVisibility(View.GONE);
                    dialogDoubleAuth.setMessage(getString(R.string.the_authentication_was_successfully_completed));
                    setDoubleAuth(true);
                }else {
                    digLnrProgress.setVisibility(View.GONE);
                    dialogDoubleAuth.setMessage(getString(R.string.authentication_failed));
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
            snd.addRequestAndSend(Network.DOUBLE_AUTH_ONE_STEP, Constants.URL_SECURING_DOUBLE_AUTH + DataTransfer.getToken() + "&action=SendCode");
        } else if (view == digBtnVerify) {
            digLnrVerify.setVisibility(View.GONE);
            dialogDoubleAuth.setMessage("");
            digLnrProgress.setVisibility(View.VISIBLE);
            String code = digEdtVerify.getText().toString();
            snd.addRequestAndSend(Network.DOUBLE_AUTH_TWO_STEP, Constants.URL_SECURING_DOUBLE_AUTH + DataTransfer.getToken() + "&action=VerifyCode&code=" + code);
        }
    }



    private void setDoubleAuth(boolean isDoubleAuthStatus){
        if (isDoubleAuthStatus) {
            lnrVerify.setVisibility(View.GONE);
            lnrBody.setVisibility(View.VISIBLE);

            getChildFragmentManager().beginTransaction().replace(R.id.fragmentContainerViewSecuring, tabsFragments[0]).commit();

            // החלפת הפרגמנטים עם הטאבים
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Fragment selectedFragment = null;
                    selectedFragment = tabsFragments[tab.getPosition()];
                    if (selectedFragment != null) {
                        getChildFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selectedFragment).commit();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });

            // כדי להציג את הפרגמנט הראשון לפני לחיצה על טאבים
            Objects.requireNonNull(tabLayout.getTabAt(0)).select();
            snd.addRequestAndSend(Network.GET_ALL_SESSIONS, Constants.URL_SECURING_GET_SESSION + DataTransfer.getToken());
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
                    active = getString(R.string.yes);
                } else {
                    active = getString(R.string.no);
                }
                String selectedDID = objSession.getString("selectedDID");
                String remoteIP = objSession.getString("remoteIP");
                String sessionType = objSession.getString("sessionType");
                String createTime = objSession.getString("createTime");
                String lastRequest = objSession.getString("lastRequest");
                String doubleAuthStatus;
                if (objSession.getBoolean("doubleAuthStatus")){
                    doubleAuthStatus = getString(R.string.yes);
                } else {
                    doubleAuthStatus = getString(R.string.no);
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