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
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.ui.layoutViews.UpdateAppView;

import org.json.JSONObject;

public class testIsExitsUser implements SendRequestForMyServer.RespondsListener {
    private final Activity act;
    private final RespondsListener respondsListener;

    @Override
    public void onSuccess(String result, String type) {
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
                respondsListener.onFailure(3, result);
                break;
        }
    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {

    }


    public interface RespondsListener {
        void onSuccess();
        void onFailure(int responseCode, String responseMessage);
    }



    public testIsExitsUser(Activity act, RespondsListener respondsListener, String mail, String password) {
        this.act = act;
        this.respondsListener = respondsListener;
    }

public void sendTest(){
    String url = Constants.URL_IS_USER_EXIT + "email=" + DataTransfer.getUsername() +"&pass="+ DataTransfer.getUid();
    Log.d("url", "url : " + url);
    new SendRequestForMyServer(act, this, "testUser", url);
}
}