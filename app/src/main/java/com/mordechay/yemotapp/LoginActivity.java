package com.mordechay.yemotapp;

import androidx.appcompat.app.AppCompatActivity;
import org.json.*;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    final String URL_HOME = "https://www.call2all.co.il/ym/api/";
    String Number;
    String Password;
    EditText edtNumber;
    String url;

    TextView resultsTextView;
    ProgressDialog progressDialog;
    Button displayData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(this);
    }

    public String getData(){
        edtNumber = findViewById(R.id.editTextNumber2);
        Number = edtNumber.getText().toString();
        edtNumber = findViewById(R.id.editTextTextPassword);
        Password = edtNumber.getText().toString();
        url = URL_HOME + "Login?username=" +Number +"&password=" +Password;
        return url;
    }

    @Override
    public void onClick(View view) {
        getData();
        new DownloadTask().execute();
    }

    public class DownloadTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("אנא המתן לאימות פרטי המערכת");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            // Fetch data from the API in the background.

            String result = "";
            try {
                URL urlConnect;
                HttpURLConnection urlConnection = null;
                try {
                    urlConnect = new URL(url);
                    //open a URL coonnection

                    urlConnection = (HttpURLConnection) urlConnect.openConnection();

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);

                    int data = isw.read();

                    while (data != -1) {
                        result += (char) data;
                        data = isw.read();

                    }

                    // return the data to onPostExecute method
                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            // dismiss the progress dialog after receiving data from API
            progressDialog.dismiss();
            try {

                JSONObject jsonObject = new JSONObject(s);
                Log.e("dijfr", jsonObject.getString("token"));
                ;
                SharedPreferences.Editor sp = getSharedPreferences("User", 0).edit();
                sp.putString("number", Number);
                sp.putString("password", Password);
                sp.putString("token", Number + ":" + Password);
                sp.commit();
                startActivity(new Intent(LoginActivity.this, homeActivity.class));
            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this, "שם המשתמש או הסיסמה אינם נכונים!", Toast.LENGTH_SHORT).show();
                Log.e("error json parse", s);
                e.printStackTrace();
            }
        }
    }

    }
