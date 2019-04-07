package com.example.unpasuserdemo.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import com.example.unpasuserdemo.JadwalMahasiswaActivity;
import com.example.unpasuserdemo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.ContentValues.TAG;
import static com.example.unpasuserdemo.services.App.CHANNEL_ID;

public class GetJadwalService extends Service {

    private NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationManager = NotificationManagerCompat.from(this);

        final ArrayList<String> jamJadwal = intent.getStringArrayListExtra("JAMJADWAL");
        ArrayList<String> jamMatakuliah = intent.getStringArrayListExtra("JAMMATAKULIAH");
        String nomor_induk = intent.getStringExtra("NOMOR_INDUK");

        Intent intentNotification = new Intent(this, JadwalMahasiswaActivity.class);
        intentNotification.putExtra("NOMOR_INDUK", nomor_induk);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intentNotification,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentBroadcast = new Intent(this,NotificationReceiver.class);
        intentBroadcast.putExtra("toastMessage", nomor_induk);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,0,intentBroadcast,PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Mata Kuliah")
                .setContentText(jamJadwal.toString())
                .setSmallIcon(R.drawable.logounpas)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setColor(Color.GREEN)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                SimpleDateFormat dtf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Calendar datetimeKalender = Calendar.getInstance();
                Date date= datetimeKalender.getTime();
                String datePhone = dtf.format(date);

                for (int i=0; i< jamJadwal.size(); i++){
                    Log.e(TAG, "resultGetJadwalForService: " + jamJadwal.get(i));
                    Date jamPhone, jadwal;
                    try {
                        jamPhone = dtf.parse(datePhone);
                        jadwal = dtf.parse(jamJadwal.get(i));
                        long diff = jadwal.getTime() - jamPhone.getTime();
                        long perbedaanMenit = diff / (60*1000)%60;
                        long perbedaanJam = diff / (60*60*1000)%60;
                        if (perbedaanJam == 0){
                            if (perbedaanMenit == 15){
                                Log.e(TAG, "resultGetJadwalForService: Tampilkan Notifikasi");
                                notificationManager.notify(1, notification);
                            } else {
                                Log.e(TAG, "resultGetJadwalForService: Jangan Tampilkan Notifikasi, perbedaan = " + perbedaanMenit);
                            }
                        } else {
                            Log.e(TAG, "resultGetJadwalForService: Kadar Luarsa");
                        }
                        Log.e(TAG, "run: " + jamPhone);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        }, 0, 1000 * 60);
        return START_REDELIVER_INTENT;
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
