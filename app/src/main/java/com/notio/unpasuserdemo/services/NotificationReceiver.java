package com.notio.unpasuserdemo.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.notio.unpasuserdemo.JadwalMahasiswaActivity;
import com.notio.unpasuserdemo.MainActivity;
import com.notio.unpasuserdemo.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.notio.unpasuserdemo.services.App.CHANNEL_ID;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String matakuliah = intent.getStringExtra("MATAKULIAH");
        String typeUser = intent.getStringExtra("TIPEUSER");
        String nomor_induk = intent.getStringExtra("NOMORINDUK");
        String message, longText;

        Log.e(TAG, "onReceive: "+ matakuliah + typeUser + nomor_induk);
        Intent intentNotification;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (typeUser.equals("9")){
            intentNotification = new Intent(context, MainActivity.class);
            message = "Persiapan mengajar " + matakuliah + " akan dimulai 30 menit lagi";
            longText = "Harap mempersiapkan diri,  kuliah " + matakuliah + " akan dimulai 30 menit lagi, Absensi bisa dilakukan 15 menit sebelum matakuliah dimulai dan toleransi 15 menit setelah matakuliah dimulai, jangan sampai terlambat, selamat Mengajar";

        } else {
            intentNotification = new Intent(context, JadwalMahasiswaActivity.class);
            message = "Persiapan belajar " + matakuliah + " akan dimulai 30 menit lagi";
            longText = "Harap memperispkan diri, kuliah " + matakuliah + " akan dimulai 30 menit lagi, Absensi bisa dilakukan 15 menit sebelum matakuliah dimulai dan toleransi keterlambatan 15 menit setelah matakuliah dimulai, jangan sampat terlambat, selamat Belajar";
        }
        intentNotification.putExtra("NOMOR_INDUK",nomor_induk);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intentNotification,PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeIcon = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.logounpas);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Jadwal Kuliah")
                .setContentText(message)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat
                        .BigTextStyle()
                        .setBigContentTitle("Persiapan untuk matakuliah " + matakuliah)
                        .bigText(longText)
                )
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
