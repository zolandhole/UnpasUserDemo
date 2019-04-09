package com.example.unpasuserdemo.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.unpasuserdemo.handlers.DBHandler;
import com.example.unpasuserdemo.utils.ServerUnpas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FeedService extends AppCompatActivity {
    private static final String TAG = "FeedService";
    private String nomor_induk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNoIndukFromDB();

    }

    private void getNoIndukFromDB() {
        DBHandler dbHandler = new DBHandler(this);
        ArrayList<HashMap<String,String>> userDb = dbHandler.getUser(1);
        for(Map<String, String> map : userDb) {
            nomor_induk = map.get("nomor_induk");
        }
        if (nomor_induk != null || !Objects.equals(nomor_induk, "")){
            String typeUser = nomor_induk.substring(0,1);
            if (!typeUser.equals("8")){
                GetDataForService();
            }
        }
        finish();
    }

    private void GetDataForService() {
        ServerUnpas serverUnpas = new ServerUnpas(this, "GetDataForService");
        synchronized (FeedService.this){
            serverUnpas.getDataJadwal(nomor_induk);
        }
    }

    public void resultGetDataForService(List<String> jamJadwal, List<String> jamMatakuliah) {
        String tipeUser = nomor_induk.substring(0, 1);
        String jamMulai;
        String matakuliah;
        for (int i = 0; i < jamJadwal.size(); i++) {
            jamMulai = jamJadwal.get(i);
            matakuliah = jamMatakuliah.get(i);
            Log.e(TAG, "setAlarm: " + jamMulai);
            String[] arr = jamMulai.split(":");
            int hours = Integer.parseInt(arr[0]);
            int minutes = Integer.parseInt(arr[1]);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hours);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            if (!cal.before(Calendar.getInstance())) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, NotificationReceiver.class);
                intent.putExtra("MATAKULIAH", matakuliah);
                intent.putExtra("TIPEUSER", tipeUser);
                intent.putExtra("NOMORINDUK", nomor_induk);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                Log.e(TAG, "resultGetDataForService: "+ cal.getTimeInMillis());
            } else {
                Log.e(TAG, "setAlarm: Jadwal Kuliah sudah lewat=" + i);
            }
        }
    }

}
