package com.mordechay.yemotapp.ui.layoutViews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.sendApiRequest;

public class UpdateAppView implements View.OnClickListener {
    private final Dialog dialog;
    private final Activity act;

    public UpdateAppView(Activity act){
        this.act = act;
        dialog = new Dialog(act);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.layout_update_app);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.update_app_download).setOnClickListener(this);

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
        if (v.getId() == R.id.update_app_download){
            Intent view = new Intent();
            view.setAction(Intent.ACTION_VIEW);
            view.setData(Uri.parse("https://mitmachim.top/"));
            act.startActivity(view);
        }
    }
}
