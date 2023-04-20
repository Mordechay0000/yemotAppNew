package com.mordechay.yemotapp.network;

import android.app.Activity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mordechay.yemotapp.BuildConfig;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsMyServListener;
import com.mordechay.yemotapp.ui.layoutViews.ErrorNoInternetView;
import com.mordechay.yemotapp.ui.layoutViews.UpdateAppView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SendRequestForMyServer {
    private final String  type;
    private final Activity act;
    private final OnRespondsMyServListener respondsListener;

    private final String networkurl;
    private JSONObject jsonObject = null;
    private final UpdateAppView updateAppView;

    public SendRequestForMyServer(Activity act, OnRespondsMyServListener respondsListener, String type, String netnetworkUrl) {
        this.act = act;
        this.respondsListener = respondsListener;
        this.type = type;
        this.networkurl = netnetworkUrl;
        this.updateAppView = new UpdateAppView(act);
        sendRequest();
    }

    private void sendRequest() {
            Log.d("url", "url" + networkurl);
            StringRequest jsObjRequest = new StringRequest(Request.Method.GET, networkurl + "&version=" + BuildConfig.VERSION_CODE,
                    response ->
                    {
                        if(response.equals("update")){
                            updateAppView.show();
                        }else{
                            respondsListener.onSuccess(response, this.type);
                        }
                    },
                    error -> {
                        // dismiss the progress dialog after receiving Constants from API
                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            int code = response.statusCode;

                            String errorMsg = new String(response.data);
                            try {
                                jsonObject = new JSONObject(errorMsg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (jsonObject != null) {
                                if (!jsonObject.isNull("message")) {
                                    String msg = jsonObject.optString("message");
                                    respondsListener.onFailure(this.networkurl, code, msg);
                                }
                            }


                        } else {
                            String errorMsg = error.getMessage();
                            respondsListener.onFailure(this.networkurl, 0, errorMsg);
                        }
                    });

            try {
                jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                        1000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestqueue = Volley.newRequestQueue(act);
                requestqueue.add(jsObjRequest);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }