package com.mordechay.yemotapp.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.mordechay.yemotapp.ui.activitys.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class sendApiRequest implements testIsExitsUser.RespondsListener{
    private String  type;
    private final Activity act;
    private final RespondsListener respondsListener;

    private final String networkurl;
    ProgressDialog progressDialog;
    private JSONObject jsonObject = null;
    private FirebaseAuth mAuth;
    private String responseString;

    @Override
    public void onSuccess(String result) {
        if(result.equals("ok")){
            respondsListener.onSuccess(this.responseString, this.type);
        }else if (result.equals("Error: Account blocked")) {
            Toast.makeText(act, "המשתמש נחסם.", Toast.LENGTH_SHORT).show();

            mAuth.signOut();
            act.startActivity(new Intent(act, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));


        }else{
            Toast.makeText(act, "המשתמש לא רשום, זה אומר שהאפליקציה גנובה!!!", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            act.startActivity(new Intent(act, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        }

    @Override
    public void onFailure(int responseCode, String responseMessage) {

    }


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
                response -> {
                    // dismiss the progress dialog after receiving Constants from API
                    if(progressDialog != null) progressDialog.dismiss();

                    sendApiRequest.this.type = type;
                    sendApiRequest.this.responseString = response;

                    mAuth = FirebaseAuth.getInstance();
                    String mail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                    String pass = mAuth.getUid();


                    new testIsExitsUser(act, sendApiRequest.this, mail, pass);
                },
                error -> {
                    // dismiss the progress dialog after receiving Constants from API
                    if(progressDialog != null) progressDialog.dismiss();
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