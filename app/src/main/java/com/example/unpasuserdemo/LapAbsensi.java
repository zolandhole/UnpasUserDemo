package com.example.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unpasuserdemo.handlers.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LapAbsensi extends AppCompatActivity implements Spinner.OnItemSelectedListener, View.OnClickListener{

    private String TAG = "LapAbsensi";
    private ConstraintLayout clSuccess, clLoading, clFailed;
    private CardView cvRetry;
    private CardView mainCardViewSetting, mainCardViewMhsAbsen;
    private DBHandler dBHandler;
    private String koderuanganDB, namaRuanganDB;
    private TextView namaRuangan, tvMataKuliah, tvSks, tvKelas, tvJadwal;
    private String id,tanggal,jam_mulai,jam_selesai,nama_fakultas,nama_jurusan,nama_matakuliah,sks,nama_dosen;
    private String idMk;
    private String ketersediaanJadwal="";
    private String idUser, nomor_induk, nama, password, serverUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lap_absensi);
        initView();
        initObject();
        initRunning();
    }


    private void initView() {
        clSuccess = findViewById(R.id.clFinish);
        clLoading = findViewById(R.id.clData);
        clFailed = findViewById(R.id.displayFailed);
        cvRetry = findViewById(R.id.constraintTitle);

    }

    private void initObject() {
        dBHandler = new DBHandler(LapAbsensi.this);
    }

    private void initRunning() {
//        displayLoading();
//        checkInternet();
    }

    private void getUserFromDB() {
        ArrayList<HashMap<String,String>> userDb = dBHandler.getUser(1);
        for(Map<String, String> map : userDb) {
            idUser = map.get("id_server");
            nomor_induk = map.get("nomor_induk");
            nama = map.get("nama");
            nama_jurusan = map.get("nama_jurusan");
            password = map.get("password");
        }
        if (idUser == null){

        }else {

        }
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
