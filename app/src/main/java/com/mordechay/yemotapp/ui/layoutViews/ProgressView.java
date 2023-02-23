package com.mordechay.yemotapp.ui.layoutViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;

public class ProgressView {
    private final Dialog dialog;

    public ProgressView(Context cntx){
        dialog = new Dialog(cntx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_progress);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    public void show(){
        dialog.show();
        DataTransfer.setProgressViewShowing(true);
    }
    public void dismiss(){
        dialog.dismiss();
        DataTransfer.setProgressViewShowing(false);
    }
    public boolean isShowing(){
        return DataTransfer.isProgressViewShowing();
    }
}