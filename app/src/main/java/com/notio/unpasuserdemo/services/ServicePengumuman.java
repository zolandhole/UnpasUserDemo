package com.notio.unpasuserdemo.services;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.notio.unpasuserdemo.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ServicePengumuman extends FirebaseMessagingService {
    private static final String TAG = "ServicePengumuman";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0){
            Log.e(TAG, "onMessageReceived: Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateRecyclerViewMain() {
        Intent intent = new Intent();
        intent.setAction("YADIRUDIYANSAH");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendPushNotification(JSONObject jsonObject) {
        Log.e(TAG, "sendPushNotification: " + jsonObject.toString());
        try {
            JSONObject data = jsonObject.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");

            MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            updateRecyclerViewMain();
            if (imageUrl.equals("null")){
                myNotificationManager.showSmallNotification(title, message, intent);
            } else {
                myNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
