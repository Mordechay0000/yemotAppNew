package com.mordechay.yemotapp.ui.activitys;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.interfaces.OnRespondsMyServListener;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.network.Network;
import com.mordechay.yemotapp.network.SendRequestForMyServer;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadResourcesActivity extends AppCompatActivity implements View.OnClickListener, OnRespondsMyServListener {

    private SendRequestForMyServer sndMyServ;
    private ProgressBar prg;
    private Button btnDownload;
    private TextView txtVersionName;
    private TextView txtVersionCode;
    private TextView txtWhatsNew;
    private String urlPackage;
    private int btnMode;
    private long downloadID;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_DATA, 0)
                .edit().putBoolean("rspDownload", true).apply();
        Intent intent = new Intent(this, openActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        setContentView(R.layout.activity_download_resouces);

        txtVersionName = findViewById(R.id.textView51);
        txtVersionCode = findViewById(R.id.textView53);
        txtWhatsNew = findViewById(R.id.textView55);
        prg = findViewById(R.id.progressBar2);
        btnDownload = findViewById(R.id.button2);
        btnDownload.setOnClickListener(DownloadResourcesActivity.this);

        sndMyServ = SendRequestForMyServer.getInstance(getApplicationContext(), this);
        sndMyServ.addRequestAndSend(Network.GET_VERSION_INFO, Constants.URL_DOWNLOAD_RESOURCES);
    }


    @Override
    public void onSuccess(String result, int type) {
        if(type == Network.GET_VERSION_INFO){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String versionName = jsonObject.getString("versionName");
                int versionCode = jsonObject.getInt("versionCode");
                String whatsNew = jsonObject.getString("whatsNew");
                txtVersionName.setText(versionName);
                txtVersionCode.setText(String.valueOf(versionCode));
                txtWhatsNew.setText(whatsNew);
                urlPackage = jsonObject.getString("url");
                prg.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {

    }

    @Override
    public void onClick(View v) {
        if (v == btnDownload){
            if(btnMode == 0){
                btnMode = 1;
                btnDownload.setVisibility(View.GONE);
                prg.setVisibility(View.VISIBLE);
                prg.setIndeterminate(false);
                prg.setProgress(0);
                try {
                    downloadFile(urlPackage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (btnMode == 1){
                btnMode = 2;
                btnDownload.setVisibility(View.GONE);
                prg.setIndeterminate(true);
                prg.setVisibility(View.VISIBLE);

                new Thread(() -> installPackage(fileUri)).start();
            } else if (btnMode == 2) {
                btnMode = 3;
                Intent intent = new Intent(this, openActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    private void downloadFile(String urlPackage) throws IOException {
// Get the AssetManager object
        AssetManager assetManager = getAssets();

// Get the InputStream object of the zip file from the assets folder
        InputStream inputStream = assetManager.open("1.0.0.zip");

// Get the File object of the internal storage directory
        File internalStorageDir = getFilesDir();

// Create a new File object for the destination path of the zip file
        File destinationFile = new File(internalStorageDir, "1.0.0.zip");
        fileUri = Uri.fromFile(destinationFile);
// Get the OutputStream object of the destination file
        OutputStream outputStream = Files.newOutputStream(destinationFile.toPath());

// Create a buffer to read and write data
        byte[] buffer = new byte[1024];
        int length;

// Copy data from the input stream to the output stream
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

// Close both streams
        inputStream.close();
        outputStream.close();

        prg.setVisibility(View.GONE);
        btnDownload.setText(R.string.installation);
        btnDownload.setVisibility(View.VISIBLE);
    }

    private void installPackage(Uri zipFileUri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(zipFileUri, "r");

            File targetDirectory = new File(this.getApplicationContext().getFilesDir().getAbsolutePath(), Constants.RESOURCES_PATH);

            if (targetDirectory.exists()) {
                targetDirectory.delete();
                targetDirectory.mkdirs();
            } else {
                targetDirectory.mkdirs();
            }

            FileInputStream inputStream = new FileInputStream(pfd.getFileDescriptor());
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                File newFile = new File(targetDirectory, zipEntry.getName());
                FileOutputStream outputStream = new FileOutputStream(newFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = zipInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.close();
            inputStream.close();
            pfd.close();

            importSharedPreferencesFromFile(new File(targetDirectory + File.separator + Constants.DEFAULT_SHARED_PREFERENCES_FILTER + ".xml"));

            runOnUiThread(() -> {
                prg.setVisibility(View.GONE);
                btnDownload.setText(R.string.next);
                btnDownload.setVisibility(View.VISIBLE);
            });
            getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_DATA, 0)
                    .edit().putBoolean("rspDownload", true).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }








    private void importSharedPreferencesFromFile(File file) {
            try {
                FileInputStream fis = new FileInputStream(file);
// Create an XmlPullParser object
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

// Set the input stream for parsing
                xpp.setInput(fis, null);

// Initialize a map for storing key-value pairs
                Map<String, Object> map = new HashMap<>();

// Loop through the xml tags and extract key-value pairs
                int eventType = xpp.getEventType();
                String key = null;
                Object value = null;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        // Get the tag name
                        String tagName = xpp.getName();
                        // If it is a "map" tag, skip it
                        if (tagName.equals("map")) {
                            eventType = xpp.next();
                            continue;
                        }
                        // If it is a "string" tag, get its name attribute as key
                        if (tagName.equals("string")) {
                            key = xpp.getAttributeValue(null, "name");
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        // Get the text content as value
                        value = xpp.getText();
                    } else if (eventType == XmlPullParser.END_TAG) {
                        // Get the tag name
                        String tagName = xpp.getName();
                        // If it is a "map" tag, end parsing
                        if (tagName.equals("map")) {
                            break;
                        }
                        // If it is a "string" tag, put the key-value pair into the map
                        if (tagName.equals("string")) {
                            map.put(key, value);
                            key = null;
                            value = null;
                        }
                    }
                    eventType = xpp.next();
                }

// Close the input stream
                fis.close();

// Get an instance of SharedPreferences
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_FILTER, 0);

// Get an editor object for modifying the preferences
                SharedPreferences.Editor editor = pref.edit();

// Loop through the map and store each key-value pair in the editor object
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    editor.putString(entry.getKey(), (String) entry.getValue());
                }

// Save changes
                editor.apply();
            } catch (XmlPullParserException | IOException e) {
                throw new RuntimeException(e);
            }
    }






    /* private void downloadFile(String fileUrl) {
        // Extract the file name from the URL
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);

        // Define the download request
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle(fileName);
        request.setDescription("Downloading resources");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        File downloadsDir = new File(getFilesDir(), "downloads");
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }
        File destinationFile = new File(downloadsDir, fileName);

        Uri destinationUri = FileProvider.getUriForFile(this, "com.mordechay.yemotapp.provider.files", destinationFile);
        request.setDestinationUri(destinationUri);

        // Enqueue the download request
        downloadID = downloadManager.enqueue(request);
    }
     */

    public class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadId == downloadID)
            {
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = manager.query(query);
                if(cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                        prg.setVisibility(View.GONE);
                        btnDownload.setText(R.string.installation);
                        btnDownload.setVisibility(View.VISIBLE);
                    }else {
                        prg.setVisibility(View.GONE);
                        btnMode --;
                        btnDownload.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
}