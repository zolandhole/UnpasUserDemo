package com.example.unpasuserdemo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import static android.content.ContentValues.TAG;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Intent intentStart = new Intent(context, TriggerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                ContextCompat.startForegroundService(context, intentStart);
                Log.e(TAG, "onReceive: SDK minimal Oreo");
            } else {
                context.startService(intentStart);
                Log.e(TAG, "onReceive: SDK dibawah Oreo");
            }
            Log.e(TAG, "onReceive: Service Started");
        }
    }
}
