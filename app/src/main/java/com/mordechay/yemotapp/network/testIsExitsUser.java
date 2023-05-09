package com.mordechay.yemotapp.network;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mordechay.yemotapp.BuildConfig;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsMyServListener;
import com.mordechay.yemotapp.ui.layoutViews.UpdateAppView;

import org.json.JSONObject;

public class testIsExitsUser extends SendRequestForMyServer {
    private static testIsExitsUser instance;
    private RespondsListener respondsListener;

    @Override
    protected void onSuccessful(int type, String result) {
        if (result.equals("update")) {
            updateAppView.show();
        } else {
            switch (result) {
                case "ok":
                    respondsListener.onSuccess();
                    break;
                case "Error: Invalid username or password":
                    respondsListener.onFailure(1, context.getString(R.string.error_invalid_username));
                    break;
                case "Error: Account blocked":
                    respondsListener.onFailure(2, "Error: Account blocked");
                    break;
                default:
                    respondsListener.onFailure(3, result);
                    break;
            }
        }
    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {

    }


    public interface RespondsListener {
        void onSuccess();
        void onFailure(int responseCode, String responseMessage);
    }



    private testIsExitsUser(@NonNull Context context, @NonNull RespondsListener respondsListener) {
        super(context, null);
        this.respondsListener = respondsListener;
    }

    public static synchronized testIsExitsUser getInstance(@Nullable Context ctx, @Nullable RespondsListener respondsListener) {
        if (instance == null) {
            assert ctx != null;
            assert respondsListener != null;
            instance = new testIsExitsUser(ctx, respondsListener);
        }
        if (ctx != null) {
            instance.setContext(ctx);
        }
        if (respondsListener != null) {
            instance.setRespondsListener(respondsListener);
        }
        return instance;
    }

    public void sendTest(){
    String url = Constants.URL_IS_USER_EXIT + "email=" + DataTransfer.getUsername() +"&pass="+ DataTransfer.getUid();
    Log.d("url", "url : " + url);
    addRequestAndSend(Network.IS_USER_EXISTS, url);
}

    public RespondsListener getRespondsListener() {
        return respondsListener;
    }

    public void setRespondsListener(RespondsListener respondsListener) {
        this.respondsListener = respondsListener;
    }
}