package com.example.chatapp.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class OrieaNotification extends ContextWrapper {
    public static final String CHANNELID = "com.example.chatapp";
    public static final String NAME = "ChatApp";
    private NotificationManager notificationManager;


    public OrieaNotification(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {
            createChannel();
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel notificationChannel = new NotificationChannel(CHANNELID, NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }
    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return  notificationManager;
    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title, String body, PendingIntent pIntent, Uri soundUri, String icon) {
        return  new Notification.Builder(getApplicationContext(), CHANNELID)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));
    }
}
