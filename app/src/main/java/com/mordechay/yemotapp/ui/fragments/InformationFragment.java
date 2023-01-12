package com.mordechay.yemotapp.ui.fragments;

import static com.mordechay.yemotapp.data.Constants.URL_HOME;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.errors.errorHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class InformationFragment extends Fragment implements View.OnClickListener, sendApiRequest.RespondsListener, SwipeRefreshLayout.OnRefreshListener {

    String url;

    SwipeRefreshLayout swipeRefreshLayout;
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

    Button btnSaveInfo;

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

        swipeRefreshLayout = v.findViewById(R.id.info_swipeRefresh);
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

        swipeRefreshLayout.setRefreshing(true);

        btnSaveInfo = v.findViewById(R.id.button3);
        btnSaveInfo.setOnClickListener(this);


        swipeRefreshLayout.setOnRefreshListener(this);
        String urlInfo = URL_HOME + "GetSession" + "?token=" + DataTransfer.getToken();
        new sendApiRequest(getActivity(), this, "info", urlInfo);

        return v;
    }

    @Override
    public void onClick(View view) {
        if (view == btnSaveInfo) {
            try {
                url = URL_HOME + "SetCustomerDetails?token=" + DataTransfer.getToken() +
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
    }


    @Override
    public void onSuccess(String result, String type) {
        switch (type) {
            case "info":
                infoActiv(result);
                break;
            case "url":

                NavController nvc = Navigation.findNavController(requireActivity(), R.id.nvgv_fragment);

                nvc.navigate(R.id.nav_home);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getString("responseStatus").equals("OK")) {
                        Toast.makeText(getActivity(), "שגיאה בשמירת הנתונים", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    new errorHandler(getActivity(), e, result);
                }
                break;
        }
    }


    @Override
    public void onFailure(int responseCode, String responseMessage) {
        swipeRefreshLayout.setRefreshing(false);
        Log.e(String.valueOf(responseCode), responseMessage);
    }

    public void infoActiv(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            if(jsonObject.getString("responseStatus").equals("OK")) {
                DataTransfer.setInfoName(jsonObject.getString("name"));
                DataTransfer.setInfoOrganization(jsonObject.getString("organization"));
                DataTransfer.setInfoContactName(jsonObject.getString("contactName"));
                DataTransfer.setInfoPhones(jsonObject.getString("phones"));
                DataTransfer.setInfoInvoiceName(jsonObject.getString("invoiceName"));
                DataTransfer.setInfoInvoiceAddress(jsonObject.getString("invoiceAddress"));
                DataTransfer.setInfoFax(jsonObject.getString("fax"));
                DataTransfer.setInfoEmail(jsonObject.getString("email"));
                DataTransfer.setInfoCreditFile(jsonObject.getString("creditFile"));
                DataTransfer.setInfoAccessPassword(jsonObject.getString("accessPassword"));
                DataTransfer.setInfoRecordPassword(jsonObject.getString("recordPassword"));
                DataTransfer.setInfoUnits(String.valueOf(jsonObject.getDouble("units")));
                DataTransfer.setInfoUnitsExpireDate(jsonObject.getString("unitsExpireDate"));


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
            }else{
                Toast.makeText(getActivity(), "שגיאה: " + jsonObject.getString("responseStatus"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            new errorHandler(getActivity(), e);
        }


        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onRefresh() {refresh();}

    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(this);
        String urlInfo = URL_HOME + "GetSession" + "?token=" + DataTransfer.getToken();
        new sendApiRequest(getActivity(), this, "info", urlInfo);
    }
}
