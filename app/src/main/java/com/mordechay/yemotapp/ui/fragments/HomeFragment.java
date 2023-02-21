package com.mordechay.yemotapp.ui.fragments;

import static com.mordechay.yemotapp.data.Constants.URL_HOME;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.ProgressView;
import com.mordechay.yemotapp.ui.programmatically.errors.errorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener, sendApiRequest.RespondsListener, SwipeRefreshLayout.OnRefreshListener {



    private final String TAG = "HomeFragment";
    String token;
    String number;
    String password;
    String urlInfo;
    String urlCall;
    String urlCamp;
    String urlCampSh;
    String urlMinu;

    ProgressView prgvStart;

    TextView txtvNumber;
    TextView txtvOrg;
    TextView txtvCall;
    TextView txtvCamp;
    TextView txtvMinu;
    TextView txtvUnits;
    CardView crdH;
    SwipeRefreshLayout swprl;
    boolean isLoading1;
    boolean isLoading2;
    boolean isLoading3;
    boolean isLoading4;
    boolean isLoading5;



    public HomeFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        swprl = v.findViewById(R.id.swipeRefresh);
        swprl.setOnRefreshListener(this);
        swprl.setRefreshing(true);


        crdH = v.findViewById(R.id.cardView);
        crdH.setOnClickListener(this);

        txtvNumber =  v.findViewById(R.id.textView5);
        txtvOrg =  v.findViewById(R.id.textView7);
        txtvCall=  v.findViewById(R.id.textView30);
        txtvCamp=  v.findViewById(R.id.textView35);
        txtvMinu=  v.findViewById(R.id.textView31);
        txtvUnits= v.findViewById(R.id.textView36);

        number = DataTransfer.getInfoNumber();
        password = DataTransfer.getInfoPassword();
        token = DataTransfer.getToken();

        urlInfo = URL_HOME + "GetSession" +"?token="+token;
        urlCall = URL_HOME + "GetIncomingCalls" +"?token="+token;
        urlCamp = URL_HOME + "GetActiveCampaigns" +"?token="+token;
        urlMinu = URL_HOME + "GetIncomingSum" +"?token="+token;
        urlCampSh = URL_HOME + "GetScheduledCampaigns" +"?token="+token + "&type=PENDING";
        prgvStart = new ProgressView(this.getContext());
prgvStart.show();

        refresh();

        return v;
    }




    @Override
    public void onClick(View view) {
        /*if(view == crdH) {
            NavController nvc = Navigation.findNavController(getActivity(), R.id.nvgv_fragment);
            nvc.navigate(R.id.nav_explorer);


        }
         */
    }



    private Fragment findFragment() {
        Fragment fragmentHost = getActivity().getSupportFragmentManager().findFragmentById(R.id.nvgv_fragment);
        List<Fragment> fragmentList = fragmentHost.getChildFragmentManager().getFragments();
        Fragment fr = fragmentList.get(fragmentList.size() - 1);
        return fr;
    }
    @Override
    public void onSuccess(String result, String type) {
        switch (type) {
            case "info":
                if(isLoading1) {
                    infoActiv(result);
                    isLoading1 = false;
                }
                break;
            case "call":
                if(isLoading2) {
                    callActiv(result);
                    isLoading2 = false;
                }
                break;
            case "camp":
                if(isLoading3) {
                    campActiv(result);
                    isLoading3 = false;
                }
                break;

            case "campsh":
                if(isLoading4) {
                    campShActiv(result);
                    isLoading4 = false;
                }
                break;

            case "minu":
                if(isLoading5 == true) {
                    minuActiv(result);
                    isLoading5 = false;
                }
                break;

        }
        if (!isLoading1 & !isLoading2 & !isLoading3 & !isLoading4 & !isLoading5) {
            if (prgvStart.isShowing()){
                prgvStart.dismiss();
            }
            swprl.setRefreshing(false);
        }
    }


    @Override
    public void onFailure(int responseCode, String responseMessage) {
        if(responseCode == 0) {
            Log.e(TAG, "no internet...");
        } else if (responseMessage != null) {
            Log.e(TAG, "code = " + responseCode + "; message = " + responseMessage);
        }else{
            Log.e(TAG, "code = " + responseCode + "; null message...");
        }
        swprl.setRefreshing(false);
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

                txtvNumber.setText(DataTransfer.getInfoNumber());
                txtvOrg.setText(DataTransfer.getInfoOrganization());
                txtvUnits.setText(DataTransfer.getInfoUnits());
            }else{
                Toast.makeText(getActivity(), "שגיאה: " + jsonObject.getString("responseStatus"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            new errorHandler(getActivity(), e);
        }
    }

    public void callActiv(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("responseStatus").equals("OK")) {
            txtvCall.setText(jsonObject.getString("callsCount"));
            }else{
            Toast.makeText(getActivity(), "שגיאה: " + jsonObject.getString("responseStatus"), Toast.LENGTH_SHORT).show();
        }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
            Log.e("error json parse Call", result + "|" + urlCall);
            e.printStackTrace();
        }
    }


    public void campActiv(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("responseStatus").equals("OK")) {

            JSONArray jsonArray = jsonObject.getJSONArray("campaigns");
            if (txtvCamp.getText().toString().equals(getText(R.string.loading))) {
                txtvCamp.setText(String.valueOf(jsonArray.length()));
            }else{
                txtvCamp.setText(txtvCamp.getText() + "   |   " + String.valueOf(jsonArray.length()));
            }
            }else{
                Toast.makeText(getActivity(), "שגיאה: " + jsonObject.getString("responseStatus"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
            Log.e("error json parse Camp", result + "|" + urlCamp);
            e.printStackTrace();
        }
    }


    public void campShActiv(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("responseStatus").equals("OK")) {
            if(txtvCamp.getText().toString().equals(getText(R.string.loading))){
                txtvCamp.setText(String.valueOf(jsonObject.getInt("totalCount")));
            }else {
                txtvCamp.setText(txtvCamp.getText() + "   |   " + String.valueOf(jsonObject.getInt("totalCount")));
            }
            }else{
                Toast.makeText(getActivity(), "שגיאה: " + jsonObject.getString("responseStatus"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
            Log.e("error json parse CampSH", result + "|" + urlCampSh);
            e.printStackTrace();
        }
    }


    public void minuActiv(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            if(jsonObject.getString("responseStatus").equals("OK")) {
                txtvMinu.setText(String.valueOf(jsonObject.getInt("direct")));
            }else{
                Toast.makeText(getActivity(), "שגיאה: " + jsonObject.getString("responseStatus"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
            Log.e("error json parse Minutes", result + "|" + urlMinu);
            e.printStackTrace();
        }
    }




    public void refresh() {
        swprl.setRefreshing(true);
        isLoading1 = true;
        isLoading2 = true;
        isLoading3 = true;
        isLoading4 = true;
        isLoading5 = true;
        txtvOrg.setText(getText(R.string.loading));
        txtvCall.setText(getText(R.string.loading));
        txtvCamp.setText(getText(R.string.loading));
        txtvMinu.setText(getText(R.string.loading));
        txtvUnits.setText(getText(R.string.loading));

        new sendApiRequest(getActivity(), this, "info", urlInfo);
        new sendApiRequest(getActivity(), this, "call", urlCall);
        new sendApiRequest(getActivity(), this, "camp", urlCamp);
        new sendApiRequest(getActivity(), this, "campsh", urlCampSh);
        new sendApiRequest(getActivity(), this, "minu", urlMinu);
    }

    @Override
    public void onRefresh() {
        refresh();
    }
}