package com.example.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unpasuserdemo.handlers.DBHandler;
import com.example.unpasuserdemo.models.ModelUser;
import com.example.unpasuserdemo.utils.ServerUnpas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

//    private String TAG = "MainActivity";
    private TextView textViewNama, textViewJurusan;
    private CardView cardViewForum, cardViewAbsen, cardViewJadwal;
    private ProgressDialog progressDialog;
    private DBHandler dbHandler;
    private String idUser, nomor_induk, nama, nama_jurusan, mac_user, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        transparentStatusBar();
        toolbarBusinness();
        initView();
        onClick();
        runningActivity();
    }

    private void toolbarBusinness() {
        Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout){
            sendUserToLoginActivity();
        }
        return true;
    }

    private void runningActivity() {
        displayLoading();
        getUserFromDB();
    }

    private void getUserFromDB() {
        ArrayList<HashMap<String,String>> userDb = dbHandler.getUser(1);
        for(Map<String, String> map : userDb) {
            idUser = map.get("id_server");
            nomor_induk = map.get("nomor_induk");
            nama = map.get("nama");
            nama_jurusan = map.get("nama_jurusan");
            mac_user = map.get("mac_user");
            password = map.get("password");
        }
        if (idUser == null){
            progressDialog.dismiss();
            sendUserToLoginActivity();
        }else {
            syncDataServerAndDB();
        }
    }

    private void syncDataServerAndDB() {
        ServerUnpas ambilData = new ServerUnpas(MainActivity.this,"syncData");
        synchronized (MainActivity.this){ambilData.getData(nomor_induk,password);}
    }

    private void displayLoading() {
        progressDialog.setTitle("Mengambil data");
        progressDialog.setMessage("Mohon menunggu, sedang memuat data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void displaySuccess() {
        progressDialog.dismiss();
    }

    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void onClick() {
        cardViewForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Forum Activity", Toast.LENGTH_SHORT).show();
            }
        });

        cardViewAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mac_user.equals("")){
                    sendUserToInputMacActivity();
                } else {
                    Toast.makeText(MainActivity.this, mac_user, Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardViewJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Jadwal Activity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        textViewNama = findViewById(R.id.main_nama);
        textViewJurusan = findViewById(R.id.main_jurusan);

        cardViewForum = findViewById(R.id.main_cv_forum);
        cardViewAbsen = findViewById(R.id.main_cv_absen);
        cardViewJadwal = findViewById(R.id.main_cv_jadwal);

        progressDialog = new ProgressDialog(this);
        dbHandler = new DBHandler(this);
    }

    public void dataUserFromServer(String id_server, String namaServer, String nama_jurusanServer, String mac_userServer, String passwordServer) {
        if (!namaServer.equals(nama) || !nama_jurusanServer.equals(nama_jurusan) || !mac_userServer.equals(mac_user) || !passwordServer.equals(password)){
            updateUserDB(id_server,namaServer,nama_jurusanServer,mac_userServer,passwordServer);
        } else {
            setObjectDisplay();
        }
    }

    private void updateUserDB(String id_server, String namaServer, String nama_jurusanServer, String mac_userServer, String passwordServer) {
        dbHandler.deleteAll();
        dbHandler.addUser(
                new ModelUser(1,id_server, nomor_induk,passwordServer,namaServer, nama_jurusanServer,mac_userServer)
        );
        dbHandler.close();
        setObjectDisplay();
    }

    private void setObjectDisplay() {
        textViewNama.setText(nama);
        textViewJurusan.setText(nama_jurusan);
        displaySuccess();
    }

    private void sendUserToLoginActivity() {
        dbHandler.deleteAll();
        Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
        intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentLogin);
        finish();
    }

    private void sendUserToInputMacActivity() {
        Intent intentInputMac = new Intent(MainActivity.this, InputMacActivity.class);
        intentInputMac.putExtra("ID_USER", idUser);
        intentInputMac.putExtra("NOMOR_INDUK", nomor_induk);
        intentInputMac.putExtra("PASSWORD", password);
        startActivity(intentInputMac);
    }
}
