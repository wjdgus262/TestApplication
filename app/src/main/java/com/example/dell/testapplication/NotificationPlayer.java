package com.example.dell.testapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;
import android.support.v7.app.NotificationCompat;

import com.squareup.picasso.Picasso;
//import android.support.v7.app.No


public class NotificationPlayer {
    private final static int NOTIFICATION_PLAYER_ID = 0x342;
    private AudioService mService;
    private NotificationManager mNotificationManager;
    private NotificationChannel notificationChannel;
    //    private NotificationManagerBuilder mNotificationManagerBuilder;
    private boolean isForeground;
    Bitmap largIcon = null;
    public NotificationPlayer(AudioService service) {
        mService = service;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationChannel = new NotificationChannel("chaneelid","channel_name",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            mNotificationManager.createNotificationChannel(notificationChannel);

        }

    }

    public void updateNotificationPlayer() {
//        cancel();
//        mNotificationManagerBuilder = new NotificationManagerBuilder();
//        mNotificationManagerBuilder.execute();
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),mService.getAudioItem().AlbumId);
                try {
                    largIcon = Picasso.with(mService).load(albumArtUri).get();
                }catch (Exception e){
                    e.printStackTrace();
                }
//                NotificationCompat.se
                Intent actionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
                Intent actionForward = new Intent(CommandActions.FORWARD);
                Intent actionRewind = new Intent(CommandActions.REWIND);
                Intent actionClose = new Intent(CommandActions.CLOSE);
                PendingIntent togglePlay = PendingIntent.getService(mService,0,actionTogglePlay,0);
                PendingIntent forward = PendingIntent.getService(mService,0,actionForward,0);
                PendingIntent rewind = PendingIntent.getService(mService,0,actionRewind,0);
                android.support.v4.app.NotificationCompat.Builder builder;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    NotificationChannel mChannel = new NotificationChannel("push","push",NotificationManager.IMPORTANCE_DEFAULT);
                    mNotificationManager.createNotificationChannel(mChannel);
                    builder = new android.support.v4.app.NotificationCompat.Builder(mService,mChannel.getId());
                }else{
                    builder = new android.support.v4.app.NotificationCompat.Builder(mService);
                }

//                NotificationCompat.Builder builder = new NotificationCompat.Builder(mService);
                builder.setContentTitle(mService.getAudioItem().title)
                        .setContentText(mService.getAudioItem().subTitle)
                        .setLargeIcon(largIcon)
                        .setAutoCancel(true)
                        .setContentIntent(PendingIntent.getActivity(mService,0,new Intent(mService,MainActivity.class),0));
                builder.addAction(new android.support.v7.app.NotificationCompat.Action(R.drawable.rewind, "", rewind));
                builder.addAction(new android.support.v7.app.NotificationCompat.Action(mService.isPlaying() ? R.drawable.pause : R.drawable.play, "", togglePlay));
                builder.addAction(new android.support.v7.app.NotificationCompat.Action(R.drawable.forward, "", forward));
                int[] actionsViewIndexs = new int[]{1,2,3};
                builder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(actionsViewIndexs));
                builder.setSmallIcon(R.drawable.empty_album_img);
                Notification notification = builder.build();
                NotificationManagerCompat.from(mService).notify(NOTIFICATION_PLAYER_ID,notification);
                if(!isForeground){
                    isForeground = true;
                    mService.startForeground(NOTIFICATION_PLAYER_ID,notification);
                }
                return null;
            }
        }.execute();
    }

    public void removeNotificationPlayer() {
//        cancel();
        mService.stopForeground(true);
        isForeground = false;
    }

//    private void cancel() {
//        if (mNotificationManagerBuilder != null) {
//            mNotificationManagerBuilder.cancel(true);
//            mNotificationManagerBuilder = null;
//        }
//    }

}
