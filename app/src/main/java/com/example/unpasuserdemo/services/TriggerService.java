package com.example.unpasuserdemo.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class TriggerService extends Service {
    private static final String TAG = "TriggerService";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: TriggerService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intentFeed = new Intent(getBaseContext(),FeedService.class);
        intentFeed.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentFeed);
        Log.e(TAG, "onStartCommand: ServiceStarted");
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Triggerservice Stopped", Toast.LENGTH_SHORT).show();
    }
}
