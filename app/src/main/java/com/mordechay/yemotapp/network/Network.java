package com.mordechay.yemotapp.network;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class Network {

    public static final int YEMOT_LOGIN = 0;
    public static final int GET_VERSION_INFO = 1;
    public static final int IS_USER_EXISTS = 2;
    public static final int GET_SMS_HISTORY = 4;
    public static final int SEND_SMS = 5;
    public static final int GET_ALL_SESSIONS = 6;
    public static final int KILL_ALL_SESSIONS = 7;
    public static final int KILL_SESSION = 8;
    public static final int GET_CALLS = 9;
    public static final int UPLOAD_TEXT_FILE = 10;
    public static final int GET_EXTENSIONS = 11;
    public static final int HOME_FRAGMENT_INFO = 12;
    public static final int HOME_FRAGMENT_CALL = 13;
    public static final int HOME_FRAGMENT_CAMP = 14;
    public static final int HOME_FRAGMENT_CAMPSH = 15;
    public static final int HOME_FRAGMENT_MINU = 16;
    public static final int GET_UNITS_HISTORY = 17;
    public static final int TRANSFER_UNITS = 18;
    public static final int GET_SIP_ACCOUNTS = 19;
    public static final int SIP_SET_CALLER_ID = 20;
    public static final int SIP_NEW_ACCOUNT = 21;
    public static final int SIP_UDP_TO_WSS = 22;
    public static final int SIP_WSS_TO_UDP = 23;
    public static final int SIP_REMOVE_ACCOUNT = 24;
    public static final int GET_TOKEN_INFORMATION = 25;
    public static final int DOUBLE_AUTH_ONE_STEP = 26;
    public static final int DOUBLE_AUTH_TWO_STEP = 27;
    public static final int GET_INFORMATION = 28;
    public static final int SAVE_INFORMATION = 29;
    public static final int SPECIAL_ID_ONE = 30;
    public static final int SPECIAL_ID_TWO = 31;
    public static final int CHANGE_PASSWORD = 32;
    public static final int INCOMING_MINUTES = 33;
    public static final int GET_SYSTEM_MESSAGES = 34;
    public static final int FILE_ACTIONS_MIN = 100;
    public static final int FILE_ACTIONS_MAX = 200;
    public static final int DOWNLOAD_FILE = 36;


    protected static Map<Integer, String> list_urls = new HashMap<>();
    protected Context context;

    protected void addRequest(int type, String url) {
        list_urls.put(type, url);
    }

    public void addArrayRequests(int[] type, String[] url) {
        for (int i = 0; i < type.length; i++)
            list_urls.put(type[i], url[i]);
    }

    public void removeRequest(int type) {
        list_urls.remove(type);
    }

    public void removeArrayRequest(int[] type) {
        for (int key : type) list_urls.remove(key);
    }

    public void sendRequests() {
        for (Map.Entry<Integer, String> entry : list_urls.entrySet()) {
            sendRequest(entry.getKey(), entry.getValue());
        }
    }

    public void addRequestAndSend(int type, String url) {
        list_urls.put(type, url);
        sendRequest(type, url);
    }

    public void sendRequestNoAdded(int type, String url) {
        sendRequest(type, url);
    }

    protected void sendRequest(int type, String url) {
        assert context != null;
        StringRequest jsObjRequest = new StringRequest(Request.Method.GET, url,
                response ->
                {
                    list_urls.remove(type);
                    try {
                        onSuccessful(type, response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    // dismiss the progress dialog after receiving Constants from API
                    NetworkResponse response = error.networkResponse;
                    if (response != null) {
                        int code = response.statusCode;

                        String errorMsg = new String(response.data);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(errorMsg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if (jsonObject != null) {
                            if (!jsonObject.isNull("message")) {
                                String msg = jsonObject.optString("message");
                                onFailure(url, code, msg);
                            }
                        }


                    } else {
                        String errorMsg = error.getMessage();
                        onFailure(url, 0, errorMsg);
                    }
                });


        try {
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestqueue = Volley.newRequestQueue(context);
            requestqueue.add(jsObjRequest);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    protected void onSuccessful(int type, String result) throws JSONException {

    }

    protected void onFailure(String url, int errorCode, String errorMessage) {

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}