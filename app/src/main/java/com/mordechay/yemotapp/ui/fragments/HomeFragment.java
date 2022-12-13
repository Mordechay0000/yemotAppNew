package com.mordechay.yemotapp.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.errors.errorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener, sendApiRequest.RespondsListener, SwipeRefreshLayout.OnRefreshListener {




    final String URL_HOME = "https://www.call2all.co.il/ym/api/";
    String token;
    String number;
    String password;
    String urlInfo;
    String urlCall;
    String urlCamp;
    String urlCampSh;
    String urlMinu;
    String urlCustom;

    String newPassword;

    SharedPreferences sp;
    SharedPreferences.Editor sped;
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

        ;

        swprl = v.findViewById(R.id.swipeRefresh);
        swprl.setOnRefreshListener(this);
        swprl.setRefreshing(true);

        sp = getActivity().getSharedPreferences("User", 0);
        sped = sp.edit();

        crdH = v.findViewById(R.id.cardView);
        crdH.setOnClickListener(this);

        txtvNumber =  v.findViewById(R.id.textView5);
        txtvOrg =  v.findViewById(R.id.textView7);
        txtvCall=  v.findViewById(R.id.textView30);
        txtvCamp=  v.findViewById(R.id.textView35);
        txtvMinu=  v.findViewById(R.id.textView31);
        txtvUnits= v.findViewById(R.id.textView36);

        number = sp.getString("number","");
        password = sp.getString("password","");
        token = number + ":" + password;

        urlInfo = URL_HOME + "GetSession" +"?token="+token;
        urlCall = URL_HOME + "GetIncomingCalls" +"?token="+token;
        urlCamp = URL_HOME + "GetActiveCampaigns" +"?token="+token;
        urlMinu = URL_HOME + "GetIncomingSum" +"?token="+token;
        urlCampSh = URL_HOME + "GetScheduledCampaigns" +"?token="+token + "&type=PENDING";



        isLoading1 = true;
        isLoading2 = true;
        isLoading3 = true;
        isLoading4 = true;
        isLoading5 = true;
        new sendApiRequest(getActivity(), this, "info", urlInfo);
        new sendApiRequest(getActivity(), this, "call", urlCall);
        new sendApiRequest(getActivity(), this, "camp", urlCamp);
        new sendApiRequest(getActivity(), this, "campsh", urlCampSh);
        new sendApiRequest(getActivity(), this, "minu", urlMinu);

        return v;
    }




    @Override
    public void onClick(View view) {
        if(view == crdH) {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            NavController nvc = Navigation.findNavController(getActivity(), R.id.nvgv_fragment);
            nvc.navigate(R.id.nav_explorer);
        }
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
                infoActiv(result);
                isLoading1 = false;
                break;
            case "call":
                callActiv(result);
                isLoading2 = false;

                break;
            case "camp":
                campActiv(result);
                isLoading3 = false;

                break;

            case "campsh":
                campShActiv(result);
                isLoading4 = false;

                break;

            case "minu":
                minuActiv(result);
                isLoading5 = false;

                break;

        }
        if (!isLoading1 & !isLoading2 & !isLoading3 & !isLoading4 & !isLoading5) {
            swprl.setRefreshing(false);
        }
    }















    @Override
    public void onFailure(int responseCode, String responseMessage) {
        Log.e(String.valueOf(responseCode), responseMessage);
        swprl.setRefreshing(false);
        refresh();
    }



















    public void infoActiv(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            if(jsonObject.getString("responseStatus").equals("OK")) {
                sped.putString("name", jsonObject.getString("name"));
                sped.putString("organization", jsonObject.getString("organization"));
                sped.putString("contactName", jsonObject.getString("contactName"));
                sped.putString("phones", jsonObject.getString("phones"));
                sped.putString("invoiceName", jsonObject.getString("invoiceName"));
                sped.putString("invoiceAddress", jsonObject.getString("invoiceAddress"));
                sped.putString("fax", jsonObject.getString("fax"));
                sped.putString("email", jsonObject.getString("email"));
                sped.putString("creditFile", jsonObject.getString("creditFile"));
                sped.putString("accessPassword", jsonObject.getString("accessPassword"));
                sped.putString("recordPassword", jsonObject.getString("recordPassword"));

            sped.putString("units", String.valueOf(jsonObject.getDouble("units")));
            sped.putString("unitsExpireDate", jsonObject.getString("unitsExpireDate"));
            sped.commit();

            txtvNumber.setText(sp.getString("number", ""));
            txtvOrg.setText(sp.getString("organization", ""));
            txtvUnits.setText(sp.getString("units", ""));
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