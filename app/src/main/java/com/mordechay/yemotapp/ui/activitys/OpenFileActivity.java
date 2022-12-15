package com.mordechay.yemotapp.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.ui.programmatically.media.VoicePlay;

public class OpenFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);


        if (DataTransfer.getFileUrl() == null || DataTransfer.getFileUrl().isEmpty() || DataTransfer.getFileType() == null || DataTransfer.getFileType().isEmpty()) {
            Toast.makeText(this, "שגיאה לא התקבל נתיב לקובץ או סוג קובץ לא מזוהה.", Toast.LENGTH_LONG).show();
            Log.e("error", DataTransfer.getFileUrl() + "        " +  DataTransfer.getFileType());
            finish();
        }



            startMediaPlayerActivity();


    }

    private void startMediaPlayerActivity() {

        Toast.makeText(this, "מפעיל", Toast.LENGTH_SHORT).show();
        // Create an Intent to start the MediaPlayerService
        Intent intent = new Intent(this, VoicePlay.class);

// Start the service
        startService(intent);
    }
}