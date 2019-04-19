package com.notio.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.notio.unpasuserdemo.utils.ServerUnpas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PengumumanActivity extends AppCompatActivity {

    AppCompatSpinner asc_tujuan, asc_dosen, asc_mahasiswa;
    LinearLayout ll_tujuan, ll_dosen, ll_mahasiswa;
    Button button_back, button_next, button_finish;
    ProgressBar progressBar;
    private String nomor_induk;
    private String NAMA;
    private String IDFAKULTAS;
    private String IDMATAKULIAH;
    private String IDJURUSAN;
    private String KEPADA;
    private String NAMAFAKULTAS;
    private static final String TAG = "PengumumanActivity";
    private JSONArray jsonArrayMatakuliah, jsonArrayJurusan;
    private RelativeLayout relativeLayoutTujuan, relativeLayoutPesan;
    private TextView textViewTujuan;
    private EditText edittextPes;

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
        button_finish = findViewById(R.id.peng_button_finish);

        relativeLayoutPesan = findViewById(R.id.relativePesan);
        relativeLayoutTujuan = findViewById(R.id.relativeTujuan);

        edittextPes = findViewById(R.id.edittextPesan);

        textViewTujuan = findViewById(R.id.textViewTujuan);

        progressBar = findViewById(R.id.peng_progressBar);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textButton = button_back.getText().toString().toLowerCase();
                if (textButton.equals("kembali")){
                    finish();
                } else {
                    relativeLayoutTujuan.setVisibility(View.VISIBLE);
                    relativeLayoutPesan.setVisibility(View.GONE);
                    button_finish.setVisibility(View.GONE);
                    button_next.setVisibility(View.VISIBLE);
                    button_back.setText(R.string.kembali);
                }
            }
        });
        button_next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                relativeLayoutTujuan.setVisibility(View.GONE);
                relativeLayoutPesan.setVisibility(View.VISIBLE);
                if (!IDMATAKULIAH.equals("999") && IDJURUSAN.equals("999")){
                    if (IDMATAKULIAH.equals("0" + IDFAKULTAS)){
                        textViewTujuan.setText("Kepada " + KEPADA + " pengampu matakuliah di Fakultas " + NAMAFAKULTAS);
                    } else {
                        textViewTujuan.setText("Kepada Dosen " + KEPADA);
                    }
                } else if (IDMATAKULIAH.equals("999") && !IDJURUSAN.equals("999")){
                    textViewTujuan.setText("Kepada Mahasiswa " + KEPADA + " Fakultas " + NAMAFAKULTAS);
                } else if (IDMATAKULIAH.equals("0" + IDFAKULTAS) && IDJURUSAN.equals("0" + IDFAKULTAS)){
                    textViewTujuan.setText("Kepada Seluruh Member Fakultas " + NAMAFAKULTAS);
                }
                button_next.setVisibility(View.GONE);
                button_back.setText("Ubah Tujuan");
                edittextPes.requestFocus();
            }
        });
        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PESAN = edittextPes.getText().toString();
                sendMessageToServer(nomor_induk,IDMATAKULIAH,IDJURUSAN,PESAN);
            }
        });
    }

    private void sendMessageToServer(String nomor_induk, String idmatakuliah, String idjurusan, String pesan) {
        ServerUnpas serverUnpas = new ServerUnpas(PengumumanActivity.this, "sendMessageToServer");
        synchronized (PengumumanActivity.this){
            serverUnpas.sendEnamString(nomor_induk,NAMA,IDFAKULTAS,idmatakuliah,idjurusan,pesan);
        }
    }

    private void initListener() {
        nomor_induk = Objects.requireNonNull(getIntent().getExtras()).getString("NOMOR_INDUK");
        NAMA = getIntent().getExtras().getString("NAMA");
        edittextPes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0){
                    button_finish.setVisibility(View.VISIBLE);
                } else {
                    button_finish.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void initRunning() {
        String TYPEUSER = nomor_induk.substring(0,1);
        if (TYPEUSER.equals("8")){
            getIdFakultas();
            relativeLayoutPesan.setVisibility(View.GONE);
            relativeLayoutTujuan.setVisibility(View.VISIBLE);
            List<String> list = new ArrayList<>();
            list.add("Pilih...");
            list.add("Dosen");
            list.add("Mahasiswa");
            list.add("Semua");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, list);
            asc_tujuan.setAdapter(dataAdapter);
        } else if (TYPEUSER.equals("9")){
            Toast.makeText(this, "TESTING DOSEN", Toast.LENGTH_SHORT).show();
        }
        asc_tujuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tujuan = asc_tujuan.getSelectedItem().toString();
                Log.e(TAG, "onItemSelected: "+ tujuan);
                switch (tujuan){
                    case "Pilih...":
                        ll_dosen.setVisibility(View.GONE);
                        ll_mahasiswa.setVisibility(View.GONE);
                        button_next.setVisibility(View.GONE);
                        break;
                    case "Dosen":
                        ll_mahasiswa.setVisibility(View.GONE);
                        button_next.setVisibility(View.GONE);
                        getMatakuliah();
                        break;
                    case "Mahasiswa":
                        ll_mahasiswa.setVisibility(View.VISIBLE);
                        ll_dosen.setVisibility(View.GONE);
                        button_next.setVisibility(View.GONE);
                        getJurusan();
                        break;
                    case "Semua":
                        ll_dosen.setVisibility(View.GONE);
                        ll_mahasiswa.setVisibility(View.GONE);
                        button_next.setVisibility(View.VISIBLE);
                        IDMATAKULIAH = "0" + IDFAKULTAS;
                        IDJURUSAN = "0" + IDFAKULTAS;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getIdFakultas() {
        ServerUnpas serverUnpas = new ServerUnpas(PengumumanActivity.this, "getIdFakultas");
        synchronized (PengumumanActivity.this){
            serverUnpas.sendSingleString(nomor_induk);
        }
    }

    private void getJurusan() {
        progressBar.setVisibility(View.VISIBLE);
        ServerUnpas serverUnpas = new ServerUnpas(PengumumanActivity.this, "getJurusan");
        synchronized (PengumumanActivity.this){
            serverUnpas.sendDuaString(nomor_induk, IDFAKULTAS);
        }
    }

    public void resultGetIdFakultas(String idfakultas, String namafakultas) {
        IDFAKULTAS = idfakultas;
        NAMAFAKULTAS = namafakultas;
        Log.e(TAG, "resultGetIdFakultas: " + IDFAKULTAS + namafakultas);
    }

    private void getMatakuliah() {
        progressBar.setVisibility(View.VISIBLE);
        ServerUnpas serverUnpas = new ServerUnpas(PengumumanActivity.this, "getMatakuliah");
        synchronized (PengumumanActivity.this){
            serverUnpas.sendDuaString(nomor_induk, IDFAKULTAS);
        }
    }

    public void resultGetMahasiswa(JSONArray jsonArrayMatakuliahServer) {
        jsonArrayMatakuliah = jsonArrayMatakuliahServer;
        List<String> list = new ArrayList<>();
        list.add("Pilih Dosen Pengampu");
        for (int i = 0; i < jsonArrayMatakuliah.length(); i++) {
            try {
                JSONObject dataServer = jsonArrayMatakuliah.getJSONObject(i);
                list.add(dataServer.getString("nama_matakuliah"));
                list.removeAll(Collections.singletonList(""));
                list.removeAll(Collections.singletonList("null"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "resultGetMahasiswa: " + e);
            }
        }
        list.add("Semua Dosen");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, list);
        asc_dosen.setAdapter(dataAdapter);
        ll_dosen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        asc_dosen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KEPADA = asc_dosen.getSelectedItem().toString();
                Log.e(TAG, "onItemSelected: "+ KEPADA);
                switch (KEPADA) {
                    case "Pilih Dosen Pengampu":
                        button_next.setVisibility(View.GONE);
                        break;
                    case "Semua Dosen":
                        button_next.setVisibility(View.VISIBLE);
                        IDMATAKULIAH = "0" + IDFAKULTAS;
                        IDJURUSAN = "999";
                        break;
                    default:
                        button_next.setVisibility(View.VISIBLE);
                        IDMATAKULIAH = getIdMatakuliah(position);
                        IDJURUSAN = "999";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void resultGetJurusan(JSONArray jsonArrayJurusanServer) {
        jsonArrayJurusan = jsonArrayJurusanServer;
        List<String> list = new ArrayList<>();
        list.add("Pilih Jurusan Mahasiswa");
        for (int i = 0; i < jsonArrayJurusan.length(); i++) {
            try {
                JSONObject dataServer = jsonArrayJurusan.getJSONObject(i);
                list.add(dataServer.getString("nama_jurusan"));
                list.removeAll(Collections.singletonList(""));
                list.removeAll(Collections.singletonList("null"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "resultGetJurusan: " + e);
            }
        }
        list.add("Semua Jurusan");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, list);
        asc_mahasiswa.setAdapter(dataAdapter);
        ll_mahasiswa.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        asc_mahasiswa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KEPADA = asc_mahasiswa.getSelectedItem().toString();
                Log.e(TAG, "onItemSelected: " + KEPADA );
                switch (KEPADA){
                    case "Pilih Jurusan Mahasiswa":
                        button_next.setVisibility(View.GONE);
                        break;
                    case "Semua Jurusan":
                        button_next.setVisibility(View.VISIBLE);
                        IDMATAKULIAH = "999";
                        IDJURUSAN = "0" + IDFAKULTAS;
                        break;
                        default:
                            button_next.setVisibility(View.VISIBLE);
                            IDMATAKULIAH = "999";
                            IDJURUSAN = getIdJurusan(position);
                            break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getIdMatakuliah(int position) {
        String IDMATAKULIAH="";
        try {
            JSONObject jsonObject = jsonArrayMatakuliah.getJSONObject(position-1);
            IDMATAKULIAH = jsonObject.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return IDMATAKULIAH;
    }

    private String getIdJurusan(int position) {
        String IDJURUSAN="";
        try {
            JSONObject jsonObject = jsonArrayJurusan.getJSONObject(position-1);
            IDJURUSAN = jsonObject.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return IDJURUSAN;
    }

    public void resultSendMessageToServer(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}
