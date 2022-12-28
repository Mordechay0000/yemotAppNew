package com.mordechay.yemotapp.network;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.data.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class SendErrorLogToUrl {
    private ProgressDialog progressDialog;
    private MaterialAlertDialogBuilder endDialog;
    private Activity act;

    private String message;
    private JSONObject jsonObject = null;




    public SendErrorLogToUrl(Activity act, String message) {
        this.act = act;
        this.message = message;

        // display a progress dialog for good user experiance
        progressDialog = new ProgressDialog(act);
        progressDialog.setMessage("שגיאה חמורה באפליקציה."+ "\n" + " אנא המתן בזמן שהמערכת שולחת את הלוג לבדיקה."+ "\n" +"השגיאה:"+"\n\n" + message);
        progressDialog.setCancelable(false);
        progressDialog.show();
        sendRequest();
    }



    private void sendRequest() {
        String url = Constants.ERROR_URL + message;

        StringRequest jsObjRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // dismiss the progress dialog after receiving Constants from API
                        if(progressDialog != null) progressDialog.dismiss();
                        endDialog = new MaterialAlertDialogBuilder(act);
                        endDialog.setTitle("הצלחה!");
                        endDialog.setMessage("הלוג נשלח בהצלחה, עזרתם לנו לשפר את המערכת. \n לחץ אישור ליציאה.");
                        endDialog.setCancelable(true);
                        endDialog.setPositiveButton("OK", null);
                        endDialog.setOnDismissListener(dialog -> {
                            act.finish();
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (response != null) {
                            // dismiss the progress dialog after receiving Constants from API
                            if(progressDialog != null) progressDialog.dismiss();
                            endDialog = new MaterialAlertDialogBuilder(act);
                            endDialog.setTitle("שגיאה!");
                            endDialog.setMessage("אופסס שגיאה בשליחת הלוג. \n לחץ אישור ליציאה.");
                            endDialog.setCancelable(true);
                            endDialog.setPositiveButton("OK", null);
                            endDialog.setOnDismissListener(dialog -> {
                                act.finish();
                            });
                        } else {
                            endDialog = new MaterialAlertDialogBuilder(act);
                            endDialog.setTitle("שגיאה!");
                            endDialog.setMessage("אופסס לא הצלחנו לשלוח את הלוג, אנא בדקו את החיבור לאינטרנט. \n לחץ אישור ליציאה.");
                            endDialog.setCancelable(true);
                            endDialog.setPositiveButton("OK", null);
                            endDialog.setOnDismissListener(dialog -> {
                                act.finish();
                            });
                        }
                    }
                });


        try{
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestqueue = Volley.newRequestQueue(act);
            requestqueue.add(jsObjRequest);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
