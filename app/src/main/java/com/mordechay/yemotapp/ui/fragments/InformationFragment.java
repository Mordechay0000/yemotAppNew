package com.mordechay.yemotapp.ui.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.errors.errorHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class InformationFragment extends Fragment implements View.OnClickListener, sendApiRequest.RespondsListener {


    final String URL_HOME = "https://www.call2all.co.il/ym/api/";
    String token;
    String url;

    ProgressDialog progressDialog;

    EditText edtSystemNumber;
    EditText edtClientName;
    EditText edtMail;
    EditText edtNameOrg;
    EditText edtContactName;
    EditText edtPhone;
    EditText edtInvName;
    EditText edtInvAddress;
    EditText edtFax;
    EditText edtPassAccess;
    EditText edtPassRecording;

    Button saveInfo;
    private String number;
    private String password;
    private String newPassword;
    private String urlCustom;
    private View changePass;


    public InformationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_information, container, false);

        token = DataTransfer.getToken();
        number = DataTransfer.getInfoNumber();
        password = DataTransfer.getInfoPassword();

        edtSystemNumber = v.findViewById(R.id.editTextPhone);
        edtClientName = v.findViewById(R.id.editTextPhone2);
        edtMail = v.findViewById(R.id.editTextTextEmailAddress);
        edtNameOrg = v.findViewById(R.id.editTextPhone3);
        edtContactName = v.findViewById(R.id.editTextPhone4);
        edtPhone = v.findViewById(R.id.editTextPhone5);
        edtInvName = v.findViewById(R.id.editTextPhone6);
        edtInvAddress = v.findViewById(R.id.editTextTextPostalAddress2);
        edtFax = v.findViewById(R.id.editTextPhone7);
        edtPassAccess = v.findViewById(R.id.editTextPhone8);
        edtPassRecording = v.findViewById(R.id.editTextPhone9);


        edtSystemNumber.setText(DataTransfer.getInfoNumber());
        edtClientName.setText(DataTransfer.getInfoName());
        edtMail.setText(DataTransfer.getInfoEmail());
        edtNameOrg.setText(DataTransfer.getInfoOrganization());
        edtContactName.setText(DataTransfer.getInfoContactName());
        edtPhone.setText(DataTransfer.getInfoPhones());
        edtInvName.setText(DataTransfer.getInfoInvoiceName());
        edtInvAddress.setText(DataTransfer.getInfoInvoiceAddress());
        edtFax.setText(DataTransfer.getInfoFax());
        edtPassAccess.setText(DataTransfer.getInfoAccessPassword());
        edtPassRecording.setText(DataTransfer.getInfoRecordPassword());

        saveInfo = v.findViewById(R.id.button3);
        saveInfo.setOnClickListener(this);
        changePass = v.findViewById(R.id.button333);
        changePass.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        try {
            url = URL_HOME + "SetCustomerDetails?token=" + token +
                    "&name=" + URLEncoder.encode(edtClientName.getText().toString(), "utf-8") +
                    "&email=" + URLEncoder.encode(edtMail.getText().toString(), "utf-8") +
                    "&organization=" + URLEncoder.encode(edtNameOrg.getText().toString(), "utf-8") +
                    "&contactName=" + URLEncoder.encode(edtContactName.getText().toString(), "utf-8") +
                    "&phones=" + URLEncoder.encode(edtPhone.getText().toString(), "utf-8") +
                    "&invoiceName=" + URLEncoder.encode(edtInvName.getText().toString(), "utf-8") +
                    "&invoiceAddress=" + URLEncoder.encode(edtInvAddress.getText().toString(), "utf-8") +
                    "&fax=" + URLEncoder.encode(edtFax.getText().toString(), "utf-8") +
                    "&accessPassword=" + URLEncoder.encode(edtPassAccess.getText().toString(), "utf-8") +
                    "&recordPassword=" + URLEncoder.encode(edtPassRecording.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("url", url);
        new sendApiRequest(getActivity(), this, "url", url);
    }


    @Override
    public void onSuccess(String result, String type) {
        if (type.equals("url")){
        NavController nvc = Navigation.findNavController(getActivity(), R.id.nvgv_fragment);
        nvc.navigate(R.id.nav_home);

        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.getString("responseStatus").equals("OK")) {
                Toast.makeText(getActivity(), "שגיאה בשמירת הנתונים", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            new errorHandler(getActivity(), e, result);
        }}else if(type.equals("pass")){



                setPassActiv(result);

        }
    }


    @Override
    public void onFailure(int responseCode, String responseMessage) {
        Log.e(String.valueOf(responseCode), responseMessage);
    }


    public void setPassActiv(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("message").equals("OK")){
                DataTransfer.setInfoPassword(newPassword);
                password = newPassword;
                token = number + ":" + password;
                DataTransfer.setToken(token);

                AlertDialog.Builder al = new AlertDialog.Builder(getActivity());
                al.setTitle("הצלחה!");
                al.setMessage("שינוי הסיסמה ל \"" +newPassword +"\" בוצע בהצלחה");
                al.show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
            Log.e("error json parse Minutes", result + "|" + result);
            e.printStackTrace();
        }
    }


    public void setPassword(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());




        final EditText edittext = new EditText(getActivity());

        alert.setMessage("הקלד את הסיסמה החדשה");
        alert.setTitle("שינוי סיסמה");

        alert.setView(edittext);

        alert.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                newPassword = edittext.getText().toString();
                urlCustom = URL_HOME + "SetPassword" +"?token="+token + "&password=" + password + "&newPassword=" + newPassword;
                Log.e("url set pass", urlCustom);
                new sendApiRequest(getActivity(), InformationFragment.this, "pass", urlCustom);
            }
        });

        alert.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }


}
