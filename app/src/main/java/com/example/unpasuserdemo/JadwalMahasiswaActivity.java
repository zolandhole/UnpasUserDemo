package com.example.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.unpasuserdemo.adapters.AdapterJadwal;
import com.example.unpasuserdemo.models.ModelJadwal;
import com.example.unpasuserdemo.services.FeedService;
import com.example.unpasuserdemo.utils.ServerUnpas;

import java.util.Calendar;
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateJadwalEveryDay();
        initView();
        initRunning();
    }

    private void updateJadwalEveryDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE,1);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        setUpdate(calendar);
    }

    private void setUpdate(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), FeedService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,999, intent, 0);
        if (calendar.before(Calendar.getInstance())){
            Log.e(TAG, "setUpdate: Akan dieksekusi besok");
            calendar.add(Calendar.DATE,1);
        }
        alarmManager.setExact(AlarmManager.RTC,calendar.getTimeInMillis(), pendingIntent);
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
