package com.mordechay.yemotapp.ui.programmatically.errors;

import android.app.Activity;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.network.SendErrorLogToUrl;

import org.json.JSONException;

public class errorHandler {

    public errorHandler(Activity cntx, Exception e) {
        Log.e("Error", e.getMessage());
        e.printStackTrace();


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(cntx);
        builder.setTitle("Error");
        builder.setMessage("שגיאה חמורה באפליקציה לחץ אישור ליציאה"+ "\n" +"השגיאה:"+"\n\n" + e.getMessage());
        builder.setPositiveButton("OK", null);
        builder.setOnDismissListener(dialog -> {
            cntx.finish();
        });
        builder.show();
        new SendErrorLogToUrl(cntx, e.getMessage());


    }

    public errorHandler(Activity cntx, NullPointerException e) {
        Log.e("Error", e.getMessage());
        e.printStackTrace();


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(cntx);
        builder.setTitle("Error");
        builder.setMessage("שגיאה חמורה באפליקציה לחץ אישור ליציאה"+ "\n" +"השגיאה:"+"\n\n" + e.getMessage());
        builder.setPositiveButton("OK", null);
        builder.setOnDismissListener(dialog -> {
            cntx.finish();
        });
        builder.show();
        new SendErrorLogToUrl(cntx, e.getMessage());

     }

    public errorHandler(Activity cntx, JSONException e, String result) {
        Log.e("Error", e.getMessage());
        e.printStackTrace();


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(cntx);
        builder.setTitle("Error");
        builder.setMessage("שגיאה ניתוח תגובת שרת לחץ אישור ליציאה"+ "\n" +"השגיאה:"+"\n\n" + e.getMessage());
        builder.setPositiveButton("OK", null);
        builder.setOnDismissListener(dialog -> {
            cntx.finish();
        });
        builder.show();
        new SendErrorLogToUrl(cntx,  e.getMessage() + "\n\n\n result: \n" +result);
    }

}

