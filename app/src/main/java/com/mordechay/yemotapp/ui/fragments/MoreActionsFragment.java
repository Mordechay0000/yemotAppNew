package com.mordechay.yemotapp.ui.fragments;

import static com.mordechay.yemotapp.data.Constants.URL_HOME;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MoreActionsFragment extends Fragment implements View.OnClickListener, OnRespondsYmtListener, MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> {


    private Button btnSpecialId;
    private androidx.appcompat.app.AlertDialog dialogSpecialIdOne;

    private EditText edtSpecialIdNumber;
    private RadioButton rdbSpecialIdCALL, rdbSpecialIdSMS;
    private String reqId;

    private EditText edtSpecialIdNumberConfirm;

    private Button btnIncomingMinutes;
    private Button btnChangePass;
    private String newPassword;
    private String urlCustom;
    private LinearLayout lnrSpecialId;
    private LinearLayout lnrProgress;
    private LinearLayout lnrSpecialIdConfirm;
    private Button btnNext;
    private Button btnSpecialIdConfirm;

    public MoreActionsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_more_actions, container, false);

        btnSpecialId = v.findViewById(R.id.btnSpecialId);
        btnSpecialId.setOnClickListener(this);

        btnIncomingMinutes = v.findViewById(R.id.btn_incoming_minutes);
        btnIncomingMinutes.setOnClickListener(this);

        btnChangePass = v.findViewById(R.id.btn_change_password);
        btnChangePass.setOnClickListener(this);

        return v;
    }
    @Override
    public void onClick(View view) {
        if (view == btnSpecialId) {
            View v = getLayoutInflater().inflate(R.layout.dialog_special_id, null);
            MaterialAlertDialogBuilder dialogSpecialIdOneBuilder = new MaterialAlertDialogBuilder(requireContext());
            dialogSpecialIdOneBuilder.setTitle("אימות זיהוי ספיישל");
            dialogSpecialIdOneBuilder.setMessage("הזן את מספר הזיהוי הספיישל שלך");
            dialogSpecialIdOneBuilder.setView(v);
            dialogSpecialIdOne  = dialogSpecialIdOneBuilder.create();
            dialogSpecialIdOne.show();

            edtSpecialIdNumber = v.findViewById(R.id.dialog_special_id_number);
            rdbSpecialIdCALL = v.findViewById(R.id.dialog_special_id_call);
            rdbSpecialIdSMS = v.findViewById(R.id.dialog_special_id_sms);
            btnNext = v.findViewById(R.id.dialog_special_id_next);
            btnNext.setOnClickListener(this);
            lnrSpecialId = v.findViewById(R.id.lnr_special_id);
            lnrProgress = v.findViewById(R.id.lnr_progress);
            lnrSpecialIdConfirm = v.findViewById(R.id.lnr_special_id_confirm);
            btnSpecialIdConfirm = v.findViewById(R.id.btn_special_id_confirm);
            btnSpecialIdConfirm.setOnClickListener(this);
            edtSpecialIdNumberConfirm = v.findViewById(R.id.dialog_special_id_number_confirm);


        }else if (view == btnNext) {
            if (!rdbSpecialIdCALL.isChecked() && !rdbSpecialIdSMS.isChecked()) {
                Toast.makeText(getContext(), "יש לבחור אחת מהאפשרויות", Toast.LENGTH_SHORT).show();
            } else {
                dialogSpecialIdOne.setMessage("");
                lnrSpecialId.setVisibility(View.GONE);
                lnrProgress.setVisibility(View.VISIBLE);
                String specialIdNumber = edtSpecialIdNumber.getText().toString();
                String specialIdType = rdbSpecialIdCALL.isChecked() ? "CALL" : "SMS";
                new SendRequestForYemotServer(requireActivity(), this, "SpecialIdOne", Constants.URL_SPECIAL_ID_VALIDATION_CALLER_ID + DataTransfer.getToken() + "&action=send&callerId=" + specialIdNumber + "&validType=" + specialIdType);
            }
            }else if (view == btnSpecialIdConfirm) {
            dialogSpecialIdOne.setMessage("");
            lnrSpecialIdConfirm.setVisibility(View.GONE);
            lnrProgress.setVisibility(View.VISIBLE);
            String specialIdNumber = edtSpecialIdNumberConfirm.getText().toString();
            new SendRequestForYemotServer(requireActivity(), this, "SpecialIdTwo", Constants.URL_SPECIAL_ID_VALIDATION_CALLER_ID + DataTransfer.getToken() + "&action=valid&reId=" + reqId + "&code=" + specialIdNumber);

        }else if (view == btnIncomingMinutes) {

            MaterialDatePicker<Pair<Long, Long>> dialogDataPicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("בחר טווח תאריכים")
                    .build();
            dialogDataPicker.addOnPositiveButtonClickListener(this);
            dialogDataPicker.show(getChildFragmentManager(), "dialogDataPicker");
        }else if(view == btnChangePass){
            setPassword();
        }
    }



    public void setPassword(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());




        final EditText edittext = new EditText(getActivity());

        alert.setMessage("הקלד את הסיסמה החדשה");
        alert.setTitle("שינוי סיסמה");

        alert.setView(edittext);

        alert.setPositiveButton("אישור", (dialog, whichButton) -> {
            newPassword = edittext.getText().toString();
            urlCustom = URL_HOME + "SetPassword" +"?token="+DataTransfer.getToken() + "&password=" + DataTransfer.getInfoPassword() + "&newPassword=" + newPassword;
            Log.e("url set pass", urlCustom);
            new SendRequestForYemotServer(getActivity(), this, "pass", urlCustom);
        });

        alert.setNegativeButton("ביטול", null);

        alert.show();
    }

    @Override
    public void onSuccess(String result, String type) {
        switch (type) {
            case "SpecialIdOne": {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("responseStatus");
                    if (status.equals("OK")) {
                        lnrProgress.setVisibility(View.GONE);
                        lnrSpecialIdConfirm.setVisibility(View.VISIBLE);
                        reqId = jsonObject.getString("reqId");
                        dialogSpecialIdOne.setTitle("אימות זיהוי ספיישל");
                        dialogSpecialIdOne.setMessage("הזן את הקוד שנשלח אליך");
                    }else{
                        lnrProgress.setVisibility(View.GONE);
                        dialogSpecialIdOne.setMessage("שגיאה: \n" + jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "SpecialIdTwo": {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    lnrProgress.setVisibility(View.GONE);
                    String status = jsonObject.getString("responseStatus");
                    String message;
                    if (status.equals("OK")) {
                        if (jsonObject.getBoolean("status")) {
                            message = "הזיהוי ספיישל נוסף בהצלחה";
                        } else {
                            message = "הזיהוי ספיישל לא נוסף";
                        }
                    } else {
                        message = "הזיהוי ספיישל לא נוסף";
                    }
                    dialogSpecialIdOne.setMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "IncomingMinutes": {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("responseStatus");
                    if (status.equals("OK")) {
                        MaterialAlertDialogBuilder dialogIncomingMinutes = new MaterialAlertDialogBuilder(getContext());
                        dialogIncomingMinutes.setTitle("סיכום דקות נכנסות");
                        dialogIncomingMinutes.setMessage("מוצג מתאריך: " + jsonObject.getString("fromDate") + "\n" +
                                "עד תאריך: " + jsonObject.getString("toDate") + "\n" +
                                "דקות בחיוג ישיר למערכת: " + jsonObject.getInt("direct") + "\n" +
                                "דקות ממערכות אחרות: " + jsonObject.getInt("transferIn") + "\n" +
                                "דקות שיצאו למערכות אחרות: " + jsonObject.getInt("transferOut"));
                        dialogIncomingMinutes.setPositiveButton("אישור", null);
                        dialogIncomingMinutes.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            }
            case "pass":


                setPassActiv(result);

                break;
        }
    }


    public void setPassActiv(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("message").equals("ok")){
                DataTransfer.setInfoPassword(newPassword);
                DataTransfer.setToken(DataTransfer.getInfoNumber() + ":" + DataTransfer.getInfoPassword());
                SharedPreferences sp = requireActivity().getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_THIS_SYSTEM,0);
                if(sp.getBoolean("isRememberMe", false)){
                    SharedPreferences.Editor sped = sp.edit();
                    sped.putString("Password", DataTransfer.getInfoPassword());
                    sped.putString("Token", DataTransfer.getToken());
                    sped.apply();
                }

                MaterialAlertDialogBuilder al = new MaterialAlertDialogBuilder(requireActivity());
                al.setTitle("הצלחה!");
                al.setMessage("שינוי הסיסמה ל \"" +newPassword +"\" בוצע בהצלחה");
                al.show();
            }else{
                MaterialAlertDialogBuilder al = new MaterialAlertDialogBuilder(requireActivity());
                al.setTitle("שגיאה!");
                al.setMessage("שינוי הסיסמה נכשל" + "\n" + "השגיאה היא: \n \n" + jsonObject.getString("message"));
                al.show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
            Log.e("error json parse Minutes", result + "|" + result);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {

    }

    @Override
    public void onPositiveButtonClick(Pair<Long, Long> selection) {
        if (selection != null) {
            long start = selection.first;
            long end = selection.second;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String startDate = sdf.format(new Date(start));
            String endDate = sdf.format(new Date(end));
            new SendRequestForYemotServer(requireActivity(), this, "IncomingMinutes", Constants.URL_INCOMING_MINUTES + DataTransfer.getToken() + "&from=" + startDate + "&to=" + endDate);
            Log.d("tagg", Constants.URL_INCOMING_MINUTES + DataTransfer.getToken() + "&from=" + startDate + "&to=" + endDate);
        }
    }
}