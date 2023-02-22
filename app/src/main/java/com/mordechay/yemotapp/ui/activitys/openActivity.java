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

import java.util.Locale;

public class openActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the language
        setAppLanguage();

        // Set the view
        setContentView(R.layout.activity_open);


        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // Initialize Firebase Auth
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    SharedPreferences sp = getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_THIS_SYSTEM,0);

                    boolean isAgree = getSharedPreferences("agrrement", 0).getBoolean("agrrement", false);
                    if(!isAgree){
                        Intent intent = new Intent(openActivity.this, StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else if (mAuth.getCurrentUser() == null) {
                        Intent intent = new Intent(openActivity.this, loginToServerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else if(sp.getBoolean("isRememberMe", false)){
                        DataTransfer.setInfoNumber(sp.getString("Number", null));
                        DataTransfer.setInfoPassword(sp.getString("Password", null));
                        DataTransfer.setToken(sp.getString("Token", null));
                        Intent inet = new Intent(openActivity.this, homeActivity.class);
                        inet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(inet);
                    } else {
                        Intent inet = new Intent(openActivity.this, LoginActivity.class);
                        inet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(inet);
                    }
                }
            }
        }).start();
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