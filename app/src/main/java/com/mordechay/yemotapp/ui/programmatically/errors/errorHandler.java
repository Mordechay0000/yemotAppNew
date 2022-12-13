package com.mordechay.yemotapp.ui.programmatically.errors;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.activitys.LoginActivity;

import org.json.JSONException;

public class errorHandler implements sendApiRequest.RespondsListener{

    public errorHandler(Activity cntx, Exception e) {
        Log.e("Error", e.getMessage());
        e.printStackTrace();

        // TODO: send error to server, set url to send error
        new sendApiRequest(cntx, this, "error", "http://yemotapp.com/api/exception.php?message=" + e.getMessage());
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(cntx);
        builder.setTitle("Error");
        builder.setMessage("שגיאה חמורה באפליקציה לחץ אישור ליציאה"+ "\n" +"השגיאה:"+"\n\n" + e.getMessage());
        builder.setPositiveButton("OK", null);
        builder.setOnDismissListener(dialog -> {
            cntx.finish();
        });
        builder.show();


    }

    public errorHandler(Activity cntx, NullPointerException e) {
        Log.e("Error", e.getMessage());
        e.printStackTrace();

        // TODO: send error to server, set url to send error
        new sendApiRequest(cntx, this, "error", "http://yemotapp.com/api/exception.php?message=" + e.getMessage());
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(cntx);
        builder.setTitle("Error");
        builder.setMessage("שגיאה חמורה באפליקציה לחץ אישור ליציאה"+ "\n" +"השגיאה:"+"\n\n" + e.getMessage());
        builder.setPositiveButton("OK", null);
        builder.setOnDismissListener(dialog -> {
            cntx.finish();
        });
        builder.show();

     }

    public errorHandler(Activity cntx, JSONException e, String result) {
        Log.e("Error", e.getMessage());
        e.printStackTrace();

        // TODO: send error to server, set url to send error
        new sendApiRequest(cntx, this, "error", "http://yemotapp.com/api/exception.php?message=" + e.getMessage() + "&result " + result, "שגיאה, אנא המתן בזמן שהמערכת שולוחת את הלוג לשרת.");
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(cntx);
        builder.setTitle("Error");
        builder.setMessage("שגיאה ניתוח תגובת שרת לחץ אישור ליציאה"+ "\n" +"השגיאה:"+"\n\n" + e.getMessage());
        builder.setPositiveButton("OK", null);
        builder.setOnDismissListener(dialog -> {
            cntx.finish();
        });
        builder.show();
    }

    @Override
    public void onSuccess( String result, String type) {

    }

    @Override
    public void onFailure( int responseCode, String responseMessage) {

    }
}

