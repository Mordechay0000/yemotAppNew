package com.mordechay.yemotapp.network;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsMyServListener;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.ui.layoutViews.ErrorNoInternetView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SendRequestForYemotServer extends Network {
    private static SendRequestForYemotServer instance;
    private OnRespondsYmtListener onRespondsYmtListener;

    private final ErrorNoInternetView errorNoInternetView;
    private JSONObject jsonObject = null;

    private SendRequestForYemotServer(Context context, OnRespondsYmtListener onRespondsYmtListener) {
        this.context = context;
        this.onRespondsYmtListener = onRespondsYmtListener;
        this.errorNoInternetView = new ErrorNoInternetView(context, onRespondsYmtListener);
    }

    public static synchronized SendRequestForYemotServer getInstance(@Nullable Context ctx, @Nullable OnRespondsYmtListener respondsListener) {
        if (instance == null) {
            assert ctx != null;
            assert respondsListener != null;
            instance = new SendRequestForYemotServer(ctx, respondsListener);
        }
        if (ctx != null) {
            instance.setContext(ctx);
        }
        if (respondsListener != null) {
            instance.setListener(respondsListener);
        }
        return instance;
    }

    private void setListener(OnRespondsYmtListener respondsListener) {
        this.onRespondsYmtListener = respondsListener;
    }

    @Override
    protected void onSuccessful(int type, String result) throws JSONException {
        onRespondsYmtListener.onSuccess(result, type);
    }

    @Override
    protected void onFailure(String url, int errorCode, String errorMessage) {
        if (errorCode == 0) {
            if (!errorNoInternetView.isShowing()) {
                errorNoInternetView.show();
            }
        }
    }
}