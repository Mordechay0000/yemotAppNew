package com.mordechay.yemotapp.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mordechay.yemotapp.BuildConfig;
import com.mordechay.yemotapp.interfaces.OnRespondsMyServListener;
import com.mordechay.yemotapp.ui.layoutViews.UpdateAppView;

import org.json.JSONObject;

public class SendRequestForMyServer extends Network {
    private static SendRequestForMyServer instance;
    private OnRespondsMyServListener respondsListener;

    private final JSONObject jsonObject = null;
    protected final UpdateAppView updateAppView;

    protected SendRequestForMyServer(@NonNull Context ctx, @Nullable OnRespondsMyServListener respondsListener) {
        this.context = ctx;
        if (respondsListener != null) {
            this.respondsListener = respondsListener;
        }
        this.updateAppView = new UpdateAppView(ctx);
    }

    public static synchronized SendRequestForMyServer getInstance(@Nullable Context ctx, @Nullable OnRespondsMyServListener respondsListener) {
        if (instance == null) {
            assert ctx != null;
            assert respondsListener != null;
            instance = new SendRequestForMyServer(ctx, respondsListener);
        }
        if (ctx != null) {
            instance.setContext(ctx);
        }
        if (respondsListener != null) {
            instance.setListener(respondsListener);
        }
        return instance;
    }

    @Override
    protected void onSuccessful(int type, String result) {
        if (result.equals("update")) {
            updateAppView.show();
            return;
        } else {
            if(respondsListener != null) {
                respondsListener.onSuccess(result, type);
            }
        }
    }

    @Override
    protected void onFailure(String url, int errorCode, String errorMessage){
        if(respondsListener != null) {
            respondsListener.onFailure(url, errorCode, errorMessage);
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    protected void setListener(@NonNull OnRespondsMyServListener respondsListener){
        this.respondsListener = respondsListener;
    }

    @Override
    protected void sendRequest(int type, String url) {
        super.sendRequest(type, url + "&version=" + BuildConfig.VERSION_CODE);
    }
}