package com.mordechay.yemotapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class homeActivity extends AppCompatActivity implements View.OnClickListener {

    final String URL_HOME = "https://www.call2all.co.il/ym/api/";
    String token;
    String urlInfo;
    String urlCall;
    String urlCamp;
    String urlCampSh;
    String urlMinu;



    SharedPreferences sp;
    SharedPreferences.Editor sped;
    TextView resultsTextView;
    ProgressDialog progressDialog;
    Button displayData;
    TextView txtvNumber;
    TextView txtvOrg;
    TextView txtvCall;
    TextView txtvCamp;
    TextView txtvMinu;
    TextView txtvUnits;
    CardView crdH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sp = getSharedPreferences("User", 0);
        sped = sp.edit();

        crdH = (CardView) findViewById(R.id.cardView);
        crdH.setOnClickListener(this);
        txtvNumber = (TextView) findViewById(R.id.textView5);
        txtvOrg = (TextView) findViewById(R.id.textView7);
        txtvCall= (TextView) findViewById(R.id.textView30);
        txtvCamp= (TextView) findViewById(R.id.textView35);
        txtvMinu= (TextView) findViewById(R.id.textView31);
        txtvUnits= (TextView) findViewById(R.id.textView36);

        token = sp.getString("token","");
        urlInfo = URL_HOME + "GetSession" +"?token="+token;
        urlCall = URL_HOME + "GetIncomingCalls" +"?token="+token;
        urlCamp = URL_HOME + "GetActiveCampaigns" +"?token="+token;
        urlMinu = URL_HOME + "GetIncomingSum" +"?token="+token;
        urlCampSh = URL_HOME + "GetScheduledCampaigns" +"?token="+token + "&type=PENDING";

        this.findViewById(R.id.button2).setOnClickListener(this);

        new DownloadInfoTask().execute();
        new DownloadCallTask().execute();
        new DownloadCampTask().execute();
        new DownloadCampShTask().execute();
        new DownloadMinutesTask().execute();
    }

    @Override
    public void onClick(View view) {
        if(view == crdH) {
            startActivity(new Intent(this, informationActivity.class));
        }else{
            startActivity(new Intent(this, fileExplorerActivity.class));
        }
    }






















    public class DownloadInfoTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(homeActivity.this);
            progressDialog.setMessage("אנא המתן לקבלת פרטי המערכת");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL urlConnect;
                HttpURLConnection urlConnection = null;
                try {
                    urlConnect = new URL(urlInfo);
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

                sped.putString("name", jsonObject.getString("name"));
                sped.putString("organization", jsonObject.getString("organization"));
                sped.putString("contactName", jsonObject.getString("contactName"));
                sped.putString("phones", jsonObject.getString("phones"));
                sped.putString("invoiceName", jsonObject.getString("invoiceName"));
                sped.putString("invoiceAddress", jsonObject.getString("invoiceAddress"));
                sped.putString("fax", jsonObject.getString("fax"));
                sped.putString("email", jsonObject.getString("email"));
                sped.putString("creditFile", jsonObject.getString("creditFile"));
                sped.putString("accessPassword", jsonObject.getString("accessPassword"));
                sped.putString("recordPassword", jsonObject.getString("recordPassword"));


                sped.putString("units", String.valueOf(jsonObject.getDouble("units")));
                sped.putString("unitsExpireDate", jsonObject.getString("unitsExpireDate"));
                sped.commit();

                txtvNumber.setText(sp.getString("number",""));
                txtvOrg.setText(sp.getString("organization",""));
                txtvUnits.setText(sp.getString("units",""));

            } catch (JSONException e) {
                Toast.makeText(homeActivity.this, "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
                Log.e("error json parse", s + "|" + urlInfo);
                e.printStackTrace();
            }
        }
    }





















    public class DownloadCallTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(homeActivity.this);
            progressDialog.setMessage("אנא המתן לקבלת רשימת השיחות במערכת");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL urlConnect;
                HttpURLConnection urlConnection = null;
                try {
                    urlConnect = new URL(urlCall);
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

                 txtvCall.setText(jsonObject.getString("callsCount"));

            } catch (JSONException e) {
                Toast.makeText(homeActivity.this, "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
                Log.e("error json parse Call", s + "|" + urlCall);
                e.printStackTrace();
            }
        }
    }























    public class DownloadCampTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(homeActivity.this);
            progressDialog.setMessage("אנא המתן לקבלת רשימת הקמפיינים.");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL urlConnect;
                HttpURLConnection urlConnection = null;
                try {
                    urlConnect = new URL(urlCamp);
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

                JSONArray jsonArray = jsonObject.getJSONArray("campaigns");
                txtvCamp.setText(String.valueOf(jsonArray.length()));

            } catch (JSONException e) {
                Toast.makeText(homeActivity.this, "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
                Log.e("error json parse Camp", s + "|" + urlCamp);
                e.printStackTrace();
            }
        }
    }









    public class DownloadCampShTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(homeActivity.this);
            progressDialog.setMessage("אנא המתן לקבלת רשימת הקמפיינים המתוזמנים.");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL urlConnect;
                HttpURLConnection urlConnection = null;
                try {
                    urlConnect = new URL(urlCampSh);
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

                txtvCamp.setText(txtvCamp.getText() + "   |   " + String.valueOf(jsonObject.getInt("totalCount")));
            } catch (JSONException e) {
                Toast.makeText(homeActivity.this, "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
                Log.e("error json parse CampSH", s + "|" + urlCampSh);
                e.printStackTrace();
            }
        }
    }





























    public class DownloadMinutesTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(homeActivity.this);
            progressDialog.setMessage("אנא המתן לקבלת הדקות מתחילת החודש.");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL urlConnect;
                HttpURLConnection urlConnection = null;
                try {
                    urlConnect = new URL(urlMinu);
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
                txtvMinu.setText(String.valueOf(jsonObject.getInt("direct")));
            } catch (JSONException e) {
                Toast.makeText(homeActivity.this, "שגיאת ניתוח נתונים!", Toast.LENGTH_SHORT).show();
                Log.e("error json parse Minutes", s + "|" + urlMinu);
                e.printStackTrace();
            }
        }
    }
}