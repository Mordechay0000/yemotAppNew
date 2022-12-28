package com.mordechay.yemotapp.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mordechay.yemotapp.ui.programmatically.errors.errorHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class testIsExitsUser {
    private Activity act;
    private RespondsListener respondsListener;

    private String mail;
    private String password;
    private JSONObject jsonObject = null;

    public interface RespondsListener {

        public void onSuccess(String result);
        public void onFailure(int responseCode, String responseMessage);
    }



    public testIsExitsUser(Activity act, RespondsListener respondsListener, String mail, String password) {
        this.act = act;
        this.respondsListener = respondsListener;
        this.mail = mail;
        this.password = password;
        sendRequest();
    }



    private void sendRequest() {
        String url = "https://topbx.app/yemotapp770999/?username=" + mail +"&password="+password;

        Log.d("url", "url" + url);
        StringRequest jsObjRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // dismiss the progress dialog after receiving Constants from API

                        respondsListener.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            int code = response.statusCode;

                            String errorMsg = new String(response.data);
                            try {
                                jsonObject = new JSONObject(errorMsg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if(jsonObject != null){
                                if (!jsonObject.isNull("message")) {
                                    String msg = jsonObject.optString("message");
                                    respondsListener.onFailure(code, msg);
                                }
                            }


                        } else {
                            String errorMsg = error.getMessage();
                            Toast.makeText(act, "אנא בדקו את החיבור לאינטרנט ונסו שוב", Toast.LENGTH_LONG).show();
                            respondsListener.onFailure(0, errorMsg);
                        }
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