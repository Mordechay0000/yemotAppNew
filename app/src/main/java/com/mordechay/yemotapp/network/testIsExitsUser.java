package com.mordechay.yemotapp.network;

import android.app.Activity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.ui.layoutViews.UpdateAppView;

import org.json.JSONObject;

public class testIsExitsUser {
    private final Activity act;
    private final RespondsListener respondsListener;

    private final String mail;
    private final String password;
    private final UpdateAppView updateAppView;

    private JSONObject jsonObject = null;

    public interface RespondsListener {
        void onSuccess();
        void onFailure(int responseCode, String responseMessage);
    }



    public testIsExitsUser(Activity act, RespondsListener respondsListener, String mail, String password) {
        this.act = act;
        this.respondsListener = respondsListener;
        this.mail = mail;
        this.password = password;
        this.updateAppView = new UpdateAppView(act);
        sendRequest();
    }



    private void sendRequest() {
        String url = Constants.URL_IS_USER_EXIT + "email=" + mail +"&pass="+password;

        Log.d("url", "url" + url);
        StringRequest jsObjRequest = new StringRequest(Request.Method.GET,url,
                result -> {
                    switch (result) {
                        case "ok":
                            respondsListener.onSuccess();
                            break;
                        case "Error: Invalid username or password":
                            respondsListener.onFailure(1, "Error: Invalid username or password");
                            break;
                        case "Error: Account blocked":
                            respondsListener.onFailure(2, "Error: Account blocked");
                            break;
                        default:
                            updateAppView.show();
                            break;
                    }
                },
                error -> {
                    NetworkResponse response = error.networkResponse;
                    if (response != null) {
                        updateAppView.show();
                    } else {
                        String errorMsg = error.getMessage();
                        respondsListener.onFailure(0, errorMsg);
                    }
                });
        try{
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestqueue = Volley.newRequestQueue(act);
        requestqueue.add(jsObjRequest);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}