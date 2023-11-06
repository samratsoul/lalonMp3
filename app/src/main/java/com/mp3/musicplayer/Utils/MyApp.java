package com.mp3.musicplayer.Utils;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "font.otf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        createNotificationChannel();
        MobileAds.initialize(this);

    }

    void createNotificationChannel() {
        if (SDK_INT >= O) {
            NotificationManager manager;
            NotificationChannel channel;
            manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            channel = new NotificationChannel(Constant.CHANNEL_ID, Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null,null);
            channel.setLightColor(Color.GREEN);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            } else {
                Log.e("setNotificationChannel", "NotificationManager Found");
            }
        } else {
            Log.e("setNotificationChannel", "NotificationImportance Error");
        }

    }
}

