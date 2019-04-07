package com.example.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.unpasuserdemo.adapters.AdapterJadwal;
import com.example.unpasuserdemo.models.ModelJadwal;
import com.example.unpasuserdemo.services.NotificationReceiver;
import com.example.unpasuserdemo.utils.ServerUnpas;

import java.util.List;
import java.util.Objects;

public class JadwalMahasiswaActivity extends AppCompatActivity {

    private static final String TAG = "JadwalMahasiswaActivity";
    private RecyclerView recyclerView;
    private String nomor_induk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_mahasiswa);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
//        initView();
//        initRunning();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
        initRunning();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendUserToMainActivity();
    }

    private void sendUserToMainActivity() {
        Intent intentMain = new Intent(this,MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentMain);
        finish();
    }

    private void initRunning() {
        ServerUnpas getJadwalKuliah = new ServerUnpas(JadwalMahasiswaActivity.this,"getJadwalKuliah");
        synchronized (JadwalMahasiswaActivity.this) {
            getJadwalKuliah.getDataJadwal(nomor_induk);
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.jadwal_rv);

        nomor_induk = Objects.requireNonNull(getIntent().getExtras()).getString("NOMOR_INDUK");
        if (nomor_induk == null){
            Log.e(TAG, "initView: "+ nomor_induk);
        }
    }

    public void resultGetJadwal(List<ModelJadwal> listJadwal) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapterJadwal = new AdapterJadwal(listJadwal);
        recyclerView.setAdapter(adapterJadwal);
    }
}
