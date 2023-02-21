package com.mordechay.yemotapp.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import org.json.*;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private CheckBox chbRememberMe;
    private String url;
    private Button btnLogin;
    private Button btnLogout;
    private CircularProgressIndicator cpi;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the view
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        MaterialToolbar mtb = findViewById(R.id.login_mtb);
        setSupportActionBar(mtb);


                chbRememberMe = findViewById(R.id.remember_me);
                btnLogin = findViewById(R.id.login_button);
                btnLogin.setOnClickListener(this);
                btnLogout = findViewById(R.id.logout_button);
                btnLogout.setOnClickListener(this);

                cpi = findViewById(R.id.login_progress);
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
            cpi.setVisibility(View.VISIBLE);
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
                if(chbRememberMe.isChecked()){
                    SharedPreferences.Editor sped = getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_THIS_SYSTEM , 0).edit();
                    sped.putBoolean("isRememberMe", true);
                    sped.putString("Number",DataTransfer.getInfoNumber());
                    sped.putString("Password",DataTransfer.getInfoPassword());
                    sped.putString("Token",DataTransfer.getToken());
                    sped.apply();
                }
                Intent inet = new Intent(LoginActivity.this, homeActivity.class);
                inet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        cpi.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {
        cpi.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
        Log.e(String.valueOf(responseCode), responseMessage);
    }


    @Override
    protected void onStart() {
        super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                startActivity(new Intent(LoginActivity.this, loginToServerActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
    }

    }
