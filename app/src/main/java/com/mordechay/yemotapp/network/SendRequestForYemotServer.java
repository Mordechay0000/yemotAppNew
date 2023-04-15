package com.mordechay.yemotapp.network;

import android.app.Activity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.ui.layoutViews.ErrorNoInternetView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SendRequestForYemotServer {
    private final String  type;
    private final Activity act;
    private final OnRespondsYmtListener onRespondsYmtListener;

    private final String networkurl;
    private final ErrorNoInternetView errorNoInternetView;
    private JSONObject jsonObject = null;

    public SendRequestForYemotServer(Activity act, OnRespondsYmtListener onRespondsYmtListener, String type, String netnetworkUrl) {
        this.act = act;
        this.onRespondsYmtListener = onRespondsYmtListener;
        this.type = type;
        this.networkurl = netnetworkUrl;
        this.errorNoInternetView = new ErrorNoInternetView(act, onRespondsYmtListener);
        sendRequest();
    }

    private void sendRequest() {
        if (!errorNoInternetView.isShowing()) {
            Log.d("url", "url : " + networkurl);
            StringRequest jsObjRequest = new StringRequest(Request.Method.GET, networkurl,
                    response -> onRespondsYmtListener.onSuccess(response, this.type),
                    error -> {
                        Log.d("Error.Response", error.toString());
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
                                    onRespondsYmtListener.onFailure(this.networkurl, code, msg);
                                }
                            }


                        } else {
                            String errorMsg = error.getMessage();
                            onRespondsYmtListener.onFailure(this.networkurl, 0, errorMsg);
                            saveUrl();
                            if(!errorNoInternetView.isShowing()) {
                                errorNoInternetView.show();
                            }
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
        }else{
            saveUrl();
        }
    }

    private void saveUrl(){
        ArrayList<String> listUrl = DataTransfer.getListUrl();
        listUrl.add(this.networkurl);
        DataTransfer.setListUrl(listUrl);
        ArrayList<String> listType = DataTransfer.getListType();
        listType.add(this.type);
        DataTransfer.setListType(listType);
        Log.e("tag" + listUrl.size(), this.networkurl);
    }
}