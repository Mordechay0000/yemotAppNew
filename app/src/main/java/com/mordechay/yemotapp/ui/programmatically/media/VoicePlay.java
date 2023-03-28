package com.mordechay.yemotapp.ui.programmatically.media;

import android.app.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.DataTransfer;

import java.io.IOException;

public class VoicePlay extends Service {
    // MediaPlayer instance

    private MediaPlayer mediaPlayer;
    private MediaSession mediaSession;

    private NotificationChannel notificationChannel;

    // Notification builder
    private NotificationCompat.Builder builder;

    // Notification ID
    private static final int NOTIFICATION_ID = 1;
    private static final String MY_CHANNEL_ID = "com.mordechay.yemotapp.voiceplay1";

    // Handler to update the progress of the notification
    Handler notificationHandler = new Handler();

    // Runnable to update the progress of the notification
    Runnable notificationRunnable = new Runnable() {
        @Override
        public void run() {
            // Update the progress of the notification
            builder.setProgress(mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition(), false);
            // Update the notification
            startForeground(NOTIFICATION_ID, builder.build());
            // Run the Runnable again after 100 milliseconds
            notificationHandler.postDelayed(this, 100);
        }
    };




    @Override
    public void onCreate() {


        if(DataTransfer.getFileUrl() == null){
            Log.e("tagggggg", "null");

        }else {


            Log.e("tagggggg", DataTransfer.getFileUrl());
            // Create a new MediaPlayer instance
            mediaPlayer = MediaPlayer.create(this, Uri.parse(DataTransfer.getFileUrl()));

            mediaSession = new MediaSession(this, "yemot_app_media");
            mediaSession.setActive(true);
            mediaSession.setCallback(new MediaSessionCallback());

            // Set the media session metadata
            MediaMetadata metadata = new MediaMetadata.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE, DataTransfer.getFileName())
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, "מושמע מתוך המערכת מספר: " + DataTransfer.getInfoNumber())
                    .build();
            mediaSession.setMetadata(metadata);


            PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                    .setActions(
                            PlaybackState.ACTION_PLAY |
                                    PlaybackState.ACTION_PAUSE |
                                    PlaybackState.ACTION_PLAY_PAUSE
                    );
            mediaSession.setMediaButtonReceiver(null);

            // Set the media session's play/pause button intent
            MediaController controller = mediaSession.getController();
            controller.getTransportControls().play();

            // Set the media session's media button receiver
            mediaSession.setMediaButtonReceiver(
                    PendingIntent.getBroadcast(this, 0, new Intent(this, MediaButtonReceiver.class), PendingIntent.FLAG_MUTABLE));

            notificationChannel = new NotificationChannel(MY_CHANNEL_ID, "my chanle name", NotificationManager.IMPORTANCE_DEFAULT);


            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MY_CHANNEL_ID)
                    .setSmallIcon(R.drawable.baseline_audio_file_24)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(MediaSessionCompat.Token.fromToken(mediaSession.getSessionToken())));


            // Display the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Set the audio stream and volume
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // Start the media player
        mediaPlayer.start();

        notificationHandler.post(notificationRunnable);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // Stop the audio
        mediaPlayer.stop();

        // Stop the Runnable
        notificationHandler.removeCallbacks(notificationRunnable);
        // Release the MediaPlayer instance
        mediaPlayer.release();
        // Release the MediaSession instance
        mediaSession.release();

        // Cancel the notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class MediaSessionCallback extends MediaSession.Callback {
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

