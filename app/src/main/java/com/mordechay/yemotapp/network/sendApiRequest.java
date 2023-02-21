package com.mordechay.yemotapp.network;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class sendApiRequest{
    private final String  type;
    private final Activity act;
    private final RespondsListener respondsListener;

    private final String networkurl;
    private JSONObject jsonObject = null;



    public interface RespondsListener {

        void onSuccess(String result, String type);
        void onFailure(int responseCode, String responseMessage);
    }



    public sendApiRequest(Activity act, RespondsListener respondsListener, String type, String netnetworkUrl) {
        this.act = act;
        this.respondsListener = respondsListener;
        this.type = type;
        this.networkurl = netnetworkUrl;
        sendRequest();
    }

    private void sendRequest() {

        Log.d("url", "url" + networkurl);
        StringRequest jsObjRequest = new StringRequest(Request.Method.GET,networkurl,
                response -> {
            respondsListener.onSuccess(response, this.type);
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