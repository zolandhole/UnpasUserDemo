package com.example.unpasuserdemo.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.Nullable;

import static android.content.ContentValues.TAG;

public class GetJadwalService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ArrayList<String> jamJadwal = intent.getStringArrayListExtra("JAMJADWAL");
        ArrayList<String> jamMatakuliah = intent.getStringArrayListExtra("JAMMATAKULIAH");
        final String nomor_induk = intent.getStringExtra("NOMOR_INDUK");
        String tipeUser = nomor_induk.substring(0, 1);

        for (int i = 0; i < jamJadwal.size(); i++) {
            String jamMulai = jamJadwal.get(i);
            String matakuliah = jamMatakuliah.get(i);
            Log.e(TAG, "setAlarm: "+ jamMulai);
            String[] arr = jamMulai.split(":");
            int hours = Integer.parseInt(arr[0]);
            int minutes = Integer.parseInt(arr[1]);

            Log.e(TAG, "onStartCommand: "+ hours + " " + minutes);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hours);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            if (!cal.before(Calendar.getInstance())){
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intentNotification = new Intent(this,NotificationReceiver.class);
                intentNotification.putExtra("MATAKULIAH", matakuliah);
                intentNotification.putExtra("TIPEUSER", tipeUser);
                intentNotification.putExtra("NOMORINDUK", nomor_induk);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,i,intentNotification,0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),pendingIntent);
                Log.e(TAG, "setAlarm: Alarm di set ke " + cal.getTimeInMillis());
            } else {
                Log.e(TAG, "setAlarm: Jadwal Kuliah sudah lewat="+i);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
