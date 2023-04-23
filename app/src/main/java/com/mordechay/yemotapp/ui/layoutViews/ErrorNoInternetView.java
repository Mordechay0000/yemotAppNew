package com.mordechay.yemotapp.ui.layoutViews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;

import java.util.ArrayList;

public class ErrorNoInternetView implements View.OnClickListener {
    private final Dialog dialog;
    private Context context;
    OnRespondsYmtListener rsp;

    public ErrorNoInternetView(Context context, OnRespondsYmtListener rsp){
        this.context = context;
        this.rsp = rsp;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.layout_error_no_internet);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.no_internet_exit).setOnClickListener(this);
        dialog.findViewById(R.id.no_internet_try_again).setOnClickListener(this);
    }


    public void show(){
        dialog.show();
        DataTransfer.setErrorNoInternetShowing(true);
    }

    public boolean isShowing(){
        return DataTransfer.isErrorNoInternetShowing();
    }
    public void dismiss(){
        dialog.dismiss();
        DataTransfer.setErrorNoInternetShowing(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.no_internet_exit){
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        } else if (v.getId() == R.id.no_internet_try_again) {
            dismiss();
            SendRequestForYemotServer.getInstance(null, rsp).sendRequests();
        }
    }
}
