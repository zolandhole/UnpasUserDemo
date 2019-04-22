package com.notio.unpasuserdemo.services;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import java.util.Collections;

public class App extends Application {
    private static final String TAG = "App";
    public static final String CHANNEL_ID = "jadwalKuliahChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
        Log.e(TAG, "onCreate: Receive Data");
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Jadwal Kuliah Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannels(Collections.singletonList(serviceChannel));
        }
    }
}
