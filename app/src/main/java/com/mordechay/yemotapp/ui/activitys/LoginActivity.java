package com.mordechay.yemotapp.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import org.json.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.mordechay.yemotapp.*;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.errors.errorHandler;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, sendApiRequest.RespondsListener {


    private String Number;
    private String Password;
    private EditText edtNumber;
    private String url;
    private Button btnLogin;
    private Button btnLogout;
    private GoogleProgressBar gpb;
    private FirebaseAuth mAuth;
    private boolean isAgree = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the language
        setAppLanguage();

        // Set the view
        setContentView(R.layout.activity_login);



        isAgree = getSharedPreferences("agrrement", 0).getBoolean("agrrement", false);
        if(!isAgree){
            Intent intent = new Intent(this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();


            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(LoginActivity.this, loginToServerActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            } else {

                btnLogin = findViewById(R.id.login_button);
                btnLogin.setOnClickListener(this);
                btnLogout = findViewById(R.id.logout_button);
                btnLogout.setOnClickListener(this);

                gpb = findViewById(R.id.google_progress);
            }
        }

}


    public String getData(){
        edtNumber = findViewById(R.id.editTextNumber2);
        Number = edtNumber.getText().toString();
        edtNumber = findViewById(R.id.editTextTextPassword);
        Password = edtNumber.getText().toString();
        url = Constants.URL_HOME + "Login?username=" +Number +"&password=" +Password;
        return url;
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogin){
            btnLogin.setVisibility(View.GONE);
            gpb.setVisibility(View.VISIBLE);
            getData();
            new sendApiRequest(this, this, "url", url);
        }else if(view == btnLogout){
            Intent inet = new Intent(this, loginToServerActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES, 0).edit().clear().commit();
            mAuth.signOut();
            startActivity(inet);
        }

    }

    @Override
    public void onSuccess(String result, String type) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("responseStatus").equals("OK")) {
                DataTransfer.setInfoNumber(Number);
                DataTransfer.setInfoPassword(Password);
                DataTransfer.setToken(Number + ":" + Password);
                Intent inet = new Intent(LoginActivity.this, homeActivity.class);
                inet.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(inet);
            }else if(jsonObject.getString("responseStatus").equals("FORBIDDEN")){


                if(jsonObject.getString("message").equals("user name or password do not match"))

                Toast.makeText(LoginActivity.this, "שם המשתמש או הסיסמה אינם נכונים.", Toast.LENGTH_SHORT).show();
                else if(jsonObject.getString("message").equals("bruteforce protection - account locked"))
                    Toast.makeText(LoginActivity.this, "המערכת חסומה, יש להיכנס לאתר הניהול של ימות המשיח על מנת לשחרר את החסימה.", Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(LoginActivity.this,  "שגיאה לא ידועה, לא ניתן להתחבר." + "\n" + "השגיאה היא:"+  "\n" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            new errorHandler(this, e, result);
        }
        gpb.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {
        gpb.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
        Log.e(String.valueOf(responseCode), responseMessage);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(!isAgree){
            Intent intent = new Intent(this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                startActivity(new Intent(LoginActivity.this, loginToServerActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        }
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
