package com.mordechay.yemotapp.ui.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordechay.yemotapp.BuildConfig;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.Network;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;
import com.mordechay.yemotapp.ui.programmatically.errors.errorHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnRespondsYmtListener {

    private SendRequestForYemotServer snd;
    private final String TAG = "LoginActivity";
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

        snd = SendRequestForYemotServer.getInstance(this, this);
        chbRememberMe = findViewById(R.id.remember_me);
        btnLogin = findViewById(R.id.login_button);
        btnLogin.setOnClickListener(this);
        btnLogout = findViewById(R.id.logout_button);
        btnLogout.setOnClickListener(this);

        cpi = findViewById(R.id.login_progress);
    }


    public String getData() {
        edtNumber = findViewById(R.id.editTextNumber2);
        Number = edtNumber.getText().toString();
        edtNumber = findViewById(R.id.editTextTextPassword);
        Password = edtNumber.getText().toString();
        url = Constants.URL_HOME + "Login?username=" + Number + "&password=" + Password;
        return url;
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogin) {
            btnLogin.setVisibility(View.GONE);
            cpi.setVisibility(View.VISIBLE);
            getData();
            snd.addRequestAndSend(Network.YEMOT_LOGIN, url);
        } else if (view == btnLogout) {
            Intent inet = new Intent(this, loginToServerActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES, 0).edit().clear().apply();
            mAuth.signOut();
            startActivity(inet);
        }

    }

    @Override
    public void onSuccess(String result, int type) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("responseStatus").equals("OK")) {
                DataTransfer.setInfoNumber(Number);
                DataTransfer.setInfoPassword(Password);
                DataTransfer.setToken(jsonObject.getString("token"));
                if (chbRememberMe.isChecked()) {
                    SharedPreferences.Editor sped = getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_THIS_SYSTEM, 0).edit();
                    sped.putBoolean("isRememberMe", true);
                    sped.putString("Number", DataTransfer.getInfoNumber());
                    sped.putString("Password", DataTransfer.getInfoPassword());
                    sped.apply();
                }
                Intent inet = new Intent(LoginActivity.this, homeActivity.class);
                inet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inet);
            } else if (jsonObject.getString("responseStatus").equals("FORBIDDEN")) {
                if (jsonObject.getString("message").equals("user name or password do not match"))
                    Toast.makeText(LoginActivity.this, "שם המשתמש או הסיסמה אינם נכונים.", Toast.LENGTH_SHORT).show();
                else if (jsonObject.getString("message").equals("bruteforce protection - account locked"))
                    Toast.makeText(LoginActivity.this, "המערכת חסומה, יש להיכנס לאתר הניהול של ימות המשיח על מנת לשחרר את החסימה.", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(LoginActivity.this, "שגיאה לא ידועה, לא ניתן להתחבר." + "\n" + "השגיאה היא:" + "\n" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            new errorHandler(this, e, result);
        }
        cpi.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {
        cpi.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
        if (BuildConfig.DEBUG) {
            if (responseCode == 0) {
                Log.e(TAG, "no internet...");
            } else if (responseMessage != null) {
                Log.e(TAG, "code = " + responseCode + "; message = " + responseMessage);
            } else {
                Log.e(TAG, "code = " + responseCode + "; null message...");
            }
        }
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