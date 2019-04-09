package com.example.unpasuserdemo.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.example.unpasuserdemo.JadwalMahasiswaActivity;
import com.example.unpasuserdemo.MainActivity;
import com.example.unpasuserdemo.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.unpasuserdemo.services.App.CHANNEL_ID;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String matakuliah = intent.getStringExtra("MATAKULIAH");
        String typeUser = intent.getStringExtra("TIPEUSER");
        String nomor_induk = intent.getStringExtra("NOMORINDUK");
        String message;

        Log.e(TAG, "onReceive: "+ matakuliah + typeUser + nomor_induk);
        Toast.makeText(context, "TAI", Toast.LENGTH_SHORT).show();
        Intent intentNotification;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (typeUser.equals("9")){
            intentNotification = new Intent(context, MainActivity.class);
            message = "Mohon persiapan matakuliah " + matakuliah + " akan dimulai 30 menit lagi";
        } else {
            intentNotification = new Intent(context, JadwalMahasiswaActivity.class);
            message = "Persiapan matakuliah " + matakuliah + " akan dimulai 30 menit lagi, jangan sampai telat ya. Absensi sudah bisa dilakukan.";
        }
        intentNotification.putExtra("NOMOR_INDUK",nomor_induk);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intentNotification,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Jadwal Kuliah")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_unpas_notif)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setColor(Color.YELLOW)
                .setOnlyAlertOnce(true)
                .build();
        notificationManager.notify(1, notification);
    }
}
