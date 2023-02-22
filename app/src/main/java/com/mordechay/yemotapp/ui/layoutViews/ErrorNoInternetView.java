package com.mordechay.yemotapp.ui.layoutViews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.sendApiRequest;

import java.util.ArrayList;

public class ErrorNoInternetView implements View.OnClickListener {
    private final Dialog dialog;
    private final Activity act;
    sendApiRequest.RespondsListener rsp;

    public ErrorNoInternetView(Activity act, sendApiRequest.RespondsListener rsp){
        this.act = act;
        this.rsp = rsp;
        dialog = new Dialog(act);
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
        DataTransfer.setShowing(true);
    }
    public void dismiss(){
        dialog.dismiss();
        DataTransfer.setShowing(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.no_internet_exit){
            act.finish();
        } else if (v.getId() == R.id.no_internet_try_again) {
            ArrayList<String> listType = DataTransfer.getListType();
            ArrayList<String> listUrl = DataTransfer.getListUrl();
            dismiss();
            for (int i = 0; i < DataTransfer.getListType().size(); i++) {
                new sendApiRequest(act, rsp, listType.get(i), listUrl.get(i));
            }
            DataTransfer.setListType(new ArrayList<String>());
            DataTransfer.setListUrl(new ArrayList<String>());
        }
    }
}
