package com.notio.unpasuserdemo.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

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

        updateJadwalEveryDay();
        return START_REDELIVER_INTENT;
    }

    private void updateJadwalEveryDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE,1);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        setUpdate(calendar);
    }

    private void setUpdate(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), FeedService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,999, intent, 0);
        if (calendar.before(Calendar.getInstance())){
            Log.e(TAG, "setUpdate: Akan dieksekusi besok");
            calendar.add(Calendar.DATE,1);
        }
        alarmManager.setExact(AlarmManager.RTC,calendar.getTimeInMillis(), pendingIntent);
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
