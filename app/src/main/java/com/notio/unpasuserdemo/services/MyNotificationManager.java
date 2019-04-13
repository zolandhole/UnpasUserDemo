package com.notio.unpasuserdemo.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;

import com.notio.unpasuserdemo.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.app.NotificationCompat;

import static com.notio.unpasuserdemo.services.App.CHANNEL_ID;

class MyNotificationManager {
    private static final int ID_BIG_NOTIFICATION = 234;
    private static final int ID_SMALL_NOTIFICATION = 235;

    private Context context;

    MyNotificationManager(Context context){
        this.context = context;
    }

    void showBigNotification(String title, String message, String url, Intent intent){
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context,ID_BIG_NOTIFICATION,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(getBitmapFromUrl(url));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        Notification notification;
        notification = builder
                .setSmallIcon(R.drawable.ic_unpas_notif)
                .setTicker(title).setWhen(0)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setStyle(bigPictureStyle)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_unpas_notif))
                .setContentText(message)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_BIG_NOTIFICATION, notification);
    }

    private Bitmap getBitmapFromUrl(String url) {
        try {
            URL strURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) strURL.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void showSmallNotification(String title, String message, Intent intent){
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context,ID_SMALL_NOTIFICATION,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        Notification notification;
        notification = builder
                .setSmallIcon(R.drawable.ic_unpas_notif)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_unpas_notif))
                .setContentText(message)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
    }
}
