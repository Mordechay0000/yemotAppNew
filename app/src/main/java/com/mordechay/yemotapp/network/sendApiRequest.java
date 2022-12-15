package com.mordechay.yemotapp.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.mordechay.yemotapp.ui.activitys.LoginActivity;
import com.mordechay.yemotapp.ui.activitys.loginToServerActivity;
import com.mordechay.yemotapp.ui.programmatically.errors.errorHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class sendApiRequest implements testIsExitsUser.RespondsListener{
    private String  type;
    private Activity act;
    private RespondsListener respondsListener;

    private String networkurl;
    ProgressDialog progressDialog;
    private JSONObject jsonObject = null;
    private FirebaseAuth mAuth;
    private String responseString;

    @Override
    public void onSuccess(String result, String type) {
        if(result.equals("ok")){
            respondsListener.onSuccess(this.responseString, this.type);
        }else if (result.equals("block")) {
            Toast.makeText(act, "המשתמש נחסם!!!", Toast.LENGTH_SHORT).show();

            mAuth.signOut();
            act.startActivity(new Intent(act, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));


        }else{
            Toast.makeText(act, "המשתמש לא רשום, זה אומר שהאפליקציה גנובה!!!", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            act.startActivity(new Intent(act, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        }

    @Override
    public void onFailure(int responseCode, String responseMessage) {

    }


    public interface RespondsListener {

        public void onSuccess(String result, String type);
        public void onFailure(int responseCode, String responseMessage);
    }



    public sendApiRequest(Activity act, RespondsListener respondsListener, String type, String netnetworkUrl) {
        this.act = act;
        this.respondsListener = respondsListener;
        this.type = type;
        this.networkurl = netnetworkUrl;
        sendRequest();
    }




    public sendApiRequest(Activity act, RespondsListener respondsListener, String type, String netnetworkUrl, String message) {
        this.act = act;
        this.respondsListener = respondsListener;
        this.type = type;
        this.networkurl = netnetworkUrl;
        // display a progress dialog for good user experiance
        progressDialog = new ProgressDialog(act);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
        sendRequest();
    }


    private void sendRequest() {

        Log.d("url", "url" + networkurl);
        StringRequest jsObjRequest = new StringRequest(Request.Method.GET,networkurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // dismiss the progress dialog after receiving Constants from API
                        if(progressDialog != null) progressDialog.dismiss();

                        sendApiRequest.this.type = type;
                        sendApiRequest.this.responseString = response;

                        mAuth = FirebaseAuth.getInstance();
                        String mail = mAuth.getCurrentUser().getEmail();
                        String pass = mAuth.getUid();


                        new testIsExitsUser(act, sendApiRequest.this, "test", "https://topbx.app/yemotapp770999/?username="+mail +"&password="+pass);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // dismiss the progress dialog after receiving Constants from API
                        if(progressDialog != null) progressDialog.dismiss();
                        try {
                            NetworkResponse response = error.networkResponse;
                            if (response != null) {
                                int code = response.statusCode;

                                String errorMsg = new String(response.data);
                                Log.e("response", "response" + errorMsg);
                                try {
                                    jsonObject = new JSONObject(errorMsg);
                                } catch (JSONException e) {
                                    new errorHandler(act, e, errorMsg);
                                }
                                try {
                                    jsonObject = new JSONObject(errorMsg);
                                } catch (JSONException e) {
                                    new errorHandler(act, e, errorMsg);
                                }
                                if(jsonObject != null){
                                if (!jsonObject.isNull("message")) {
                                    String msg = jsonObject.optString("message");
                                    respondsListener.onFailure(code, msg);
                                }
                                }

                            } else {
                                String errorMsg = error.getMessage();
                                Toast.makeText(act, "אנא בדקו את החיבור ונסו שוב", Toast.LENGTH_SHORT).show();
                                    respondsListener.onFailure(0, errorMsg);
                                }
                        } catch (Exception e) {
                            new errorHandler(act, e);
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