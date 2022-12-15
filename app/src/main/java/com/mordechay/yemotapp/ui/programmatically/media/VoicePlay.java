package com.mordechay.yemotapp.ui.programmatically.media;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.session.MediaButtonReceiver;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;

import java.io.IOException;

public class VoicePlay extends Service {
    // MediaPlayer instance

    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSession;

    // Notification builder
    private NotificationCompat.Builder builder;

    // Notification ID
    private static final int NOTIFICATION_ID = 1;


    @Override
    public void onCreate() {


        // Create a new MediaPlayer instance
        mediaPlayer = MediaPlayer.create(this, Uri.parse(DataTransfer.getFileUrl()));


        mediaSession = new MediaSessionCompat(this, "yemot_app_media");
        mediaSession.setActive(true);
        mediaSession.setCallback(new MediaSessionCallback());

        // Set the media session metadata
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, DataTransfer.getFileName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "מושמע מתוך המערכת מספר: " + DataTransfer.getInfoNumber() )
                .build();
        mediaSession.setMetadata(metadata);

        // Set the media session's play/pause button intent
        MediaControllerCompat controller = mediaSession.getController();
        controller.getTransportControls().play();

        // Set the media session's media button receiver
        mediaSession.setMediaButtonReceiver(
                PendingIntent.getBroadcast(this, 0, new Intent(this, MediaButtonReceiver.class), 0));

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.ic_baseline_audio_file_24)
                .setContentTitle("My Music")
                .setContentText("Now playing: My Song")
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0)
                        .setMediaSession(mediaSession.getSessionToken()));

        // Display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Set the audio stream and volume
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // Start the media player
        mediaPlayer.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // Stop playback
        mediaPlayer.stop();

        // Release the media player
        mediaPlayer.release();

        // Cancel the notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            super.onPlay();
            // Start the media player
            mediaPlayer.start();
        }

        @Override
        public void onPause() {
            super.onPause();
            // Pause the media player
            mediaPlayer.pause();
        }

        @Override
        public void onStop() {
            super.onStop();
            // Stop the media player and the service
            mediaPlayer.stop();
            stopSelf();
        }
    }
    }

