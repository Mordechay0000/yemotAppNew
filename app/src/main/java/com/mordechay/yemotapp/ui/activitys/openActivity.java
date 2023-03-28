package com.mordechay.yemotapp.ui.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class openActivity extends AppCompatActivity implements OnRespondsYmtListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the language
        setAppLanguage();

        // Set the view
        setContentView(R.layout.activity_open);


        // Initialize Firebase Auth
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    SharedPreferences spThisSystem = getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_THIS_SYSTEM,0);
                    SharedPreferences spData = getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_DATA, 0);

                    boolean isResourcedDownloading = spData.getBoolean("rspDownload", false);
                    if(isResourcedDownloading) {
                        boolean isAgree = spData.getBoolean("agree", false);
                        if (!isAgree) {
                            Intent intent = new Intent(openActivity.this, StartActivity.class);
                            start(intent);
                        } else if (mAuth.getCurrentUser() == null) {
                            Intent intent = new Intent(openActivity.this, loginToServerActivity.class);
                            start(intent);
                        } else if (spThisSystem.getBoolean("isRememberMe", false)) {
                            DataTransfer.setInfoNumber(spThisSystem.getString("Number", null));
                            DataTransfer.setInfoPassword(spThisSystem.getString("Password", null));
                            new SendRequestForYemotServer(openActivity.this, openActivity.this, "login", Constants.URL_LOGIN + "username=" + DataTransfer.getInfoNumber() + "&password=" + DataTransfer.getInfoPassword());
                        } else {
                            Intent intent = new Intent(openActivity.this, LoginActivity.class);
                            start(intent);
                        }
                    }else{
                        Intent intent = new Intent(openActivity.this, DownloadResourcesActivity.class);
                        start(intent);
                    }
    }


    public void start(Intent intent){
        new Thread(new Runnable() {
@Override
public void run() {
synchronized (this) {
        try {
        wait(2000);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (InterruptedException e) {
        throw new RuntimeException(e);
        }
        }   }
        }).start();
    }


        @Override
    public void onSuccess(String result, String type) {
        if(type.equals("login")) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("responseStatus").equals("OK")) {
                    DataTransfer.setToken(jsonObject.getString("token"));
                    Intent intent = new Intent(openActivity.this, homeActivity.class);
                    start(intent);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {

    }


    // set the language app
    private void setAppLanguage() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        String lang = sp.getString("language", "default");
        Configuration config;
        config = getBaseContext().getResources().getConfiguration();
        Locale locale;
        if (!lang.equals("default")) {
            locale = new Locale(lang);
        } else {
            locale = new Locale(Locale.getDefault().getLanguage());
        }
        Locale.setDefault(locale);
        config.setLocale(locale);

        getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

}