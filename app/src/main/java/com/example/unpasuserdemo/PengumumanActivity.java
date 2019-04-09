package com.example.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.unpasuserdemo.utils.ServerUnpas;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PengumumanActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    AppCompatSpinner asc_tujuan, asc_dosen, asc_mahasiswa;
    LinearLayout ll_tujuan, ll_dosen, ll_mahasiswa;
    Button button_back, button_next;
    ProgressBar progressBar;
    private String nomor_induk;
    private static final String TAG = "PengumumanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengumuman);

        initView();
        initListener();
        initRunning();
    }

    private void initView() {
        asc_tujuan = findViewById(R.id.peng_acs_tujuan);
        asc_dosen = findViewById(R.id.peng_acs_dosen);
        asc_mahasiswa = findViewById(R.id.peng_acs_mahasiswa);

        ll_tujuan = findViewById(R.id.peng_ll_tujuan);
        ll_dosen = findViewById(R.id.peng_ll_dosen);
        ll_mahasiswa = findViewById(R.id.peng_ll_mahasiswa);

        button_back = findViewById(R.id.peng_button_back);
        button_next = findViewById(R.id.peng_button_next);

        progressBar = findViewById(R.id.peng_progressBar);

    }

    private void initListener() {
        asc_tujuan.setOnItemSelectedListener(PengumumanActivity.this);
        nomor_induk = Objects.requireNonNull(getIntent().getExtras()).getString("NOMOR_INDUK");
    }

    private void initRunning() {
        List<String> list = new ArrayList<>();
        list.add("Dosen");
        list.add("Mahasiswa");
        list.add("Semua");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout);
        asc_tujuan.setAdapter(dataAdapter);
    }

    private void displayLoading() {
        progressBar.setVisibility(View.VISIBLE);
        ll_tujuan.setVisibility(View.GONE);
        ll_dosen.setVisibility(View.GONE);
        ll_mahasiswa.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
