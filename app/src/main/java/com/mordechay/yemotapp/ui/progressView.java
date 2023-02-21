package com.mordechay.yemotapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.mordechay.yemotapp.R;

public class progressView {
    private final Dialog dialog;

    public progressView(Context cntx){
        dialog = new Dialog(cntx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_progress);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    public void show(){
        dialog.show();
    }
    public void dismiss(){
        dialog.dismiss();
    }
}
