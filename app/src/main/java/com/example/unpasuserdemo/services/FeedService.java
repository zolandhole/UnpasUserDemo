package com.example.unpasuserdemo.services;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.unpasuserdemo.handlers.DBHandler;
import com.example.unpasuserdemo.utils.ServerUnpas;

import java.util.ArrayList;
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
        Intent intentService = new Intent(this, GetJadwalService.class);
        intentService.putStringArrayListExtra("JAMJADWAL",(ArrayList<String>) jamJadwal);
        intentService.putStringArrayListExtra("JAMMATAKULIAH",(ArrayList<String>) jamMatakuliah);
        intentService.putExtra("NOMOR_INDUK", nomor_induk);
        startService(intentService);
        Log.e(TAG, "resultGetDataForService: " + jamMatakuliah);
        finish();
    }
}
