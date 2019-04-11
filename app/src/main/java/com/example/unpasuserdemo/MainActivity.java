package com.example.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unpasuserdemo.handlers.DBHandler;
import com.example.unpasuserdemo.models.ModelUser;
import com.example.unpasuserdemo.services.FeedService;
import com.example.unpasuserdemo.utils.ServerUnpas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    private String TAG = "MainActivity";
    private TextView textViewNama;
    private TextView textViewJurusan;
    private CardView cardViewForum, cardViewAbsen, cardViewJadwal, cardViewLapAbsen;
    private LinearLayout linearLayoutMenu;
    private RelativeLayout main_rl_tambah;
    private ProgressDialog progressDialog;
    private DBHandler dbHandler;
    private String idUser;
    private String nomor_induk;
    private String nama;
    private String nama_jurusan;
    private String mac_user;
    private String password;
    private String uuid;
    private String serverUUID;
    private String matikanBluetooth;
    private BluetoothAdapter bluetoothAdapter;
    private Dialog noInternetDialog;

    private final BroadcastReceiver receiverBTEnable = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.e(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.e(TAG, "onReceive: TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.e(TAG, "onReceive: STATE ON");
                        sendUserToQRActivity();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.e(TAG, "onReceive: TURNING ON");
                        break;
                }
            }
        }
    };

    private void requestEnableBT() {
        if (bluetoothAdapter == null){
            Toast.makeText(this, "Perangkat anda tidak mendukung bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!bluetoothAdapter.isEnabled()){
            matikanBluetooth = "ya";
            Intent intentBTEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intentBTEnable);

            IntentFilter intentFilterBT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiverBTEnable, intentFilterBT);
        }

        if (bluetoothAdapter.isEnabled()){
            matikanBluetooth = "tidak";
            sendUserToQRActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbarBusinness();
        initView();
        initListener();
        onClick();

        initRunning();

        uuid = "35" +
                Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, FeedService.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiverBTEnable);
        } catch (Exception ignored){

        }
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

    private void toolbarBusinness() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    private void initView() {
        textViewNama = findViewById(R.id.main_nama);
        textViewJurusan = findViewById(R.id.main_jurusan);

        cardViewForum = findViewById(R.id.main_cv_forum);
        cardViewAbsen = findViewById(R.id.main_cv_absen);
        cardViewJadwal = findViewById(R.id.main_cv_jadwal);
        cardViewLapAbsen = findViewById(R.id.main_cv_lap_absen);

        linearLayoutMenu = findViewById(R.id.main_linar_menu);

        main_rl_tambah = findViewById(R.id.main_rl_tambah);

        progressDialog = new ProgressDialog(this);
        dbHandler = new DBHandler(this);
    }

    private void initListener() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        noInternetDialog = new Dialog(MainActivity.this);
    }

    private void initRunning() {
        displayLoading();
        getUserFromDB();
        updateJadwalEveryDay();
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
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void displaySuccess() {
        progressDialog.dismiss();
    }

    public void dataUserFromServer(String id_server, String namaServer, String nama_jurusanServer, String mac_userServer, String passwordServer, String uuidServer) {
        serverUUID = uuidServer;
        if (!serverUUID.equals("")){
            // Cek UUID Siapa itu
            ServerUnpas checkOwnerUUID = new ServerUnpas(MainActivity.this, "get_owner_uuid");
            synchronized (MainActivity.this){
                checkOwnerUUID.getNamaUUID(uuidServer);
            }
        } else {
            // CEK UUID UNIT KE KE SERVER SIAPA TAU ADA YG PUNYA
            ServerUnpas checkThisUUIDtoServer = new ServerUnpas(MainActivity.this, "cek_uuid_to_server");
            synchronized (MainActivity.this){
                checkThisUUIDtoServer.sendUUID(idUser,nomor_induk,uuid);
            }
        }

        if (!namaServer.equals(nama) || !nama_jurusanServer.equals(nama_jurusan) || !mac_userServer.equals(mac_user) || !passwordServer.equals(password)){
            dbHandler.deleteAll();
            dbHandler.addUser(
                    new ModelUser(1,id_server, nomor_induk,passwordServer,namaServer, nama_jurusanServer,mac_userServer)
            );
            dbHandler.close();
            setObjectDisplay();
        }
    }

    //RESULT dari Cek UUID Siapa itu
    public void resultNamaUUIDfromServer(String namaUserDiServer){
        Log.e(TAG, "resultNamaUUIDfromServer: "+ namaUserDiServer);
        if (!serverUUID.equals(uuid)){
            showDialogUUIDExist();
        } else {
            setObjectDisplay();
        }
        progressDialog.dismiss();
    }

    //RESULT dari CEK UUID UNIT KE KE SERVER SIAPA TAU ADA YG PUNYA
    public void resultUpdateUUID(String namaPemilik) {
        if (!namaPemilik.equals("")){
            showDialogUUID(namaPemilik);
        } else {
            setObjectDisplay();
        }
        progressDialog.dismiss();
    }

    private void showDialogUUIDExist(){
        final Dialog uuidDialog = new Dialog(this);
        uuidDialog.setContentView(R.layout.wrong_uuid_layout);
        uuidDialog.setCanceledOnTouchOutside(false);
        uuidDialog.setCancelable(false);
        TextView textViewNamaUUID = uuidDialog.findViewById(R.id.uuid_nama_server);
        textViewNamaUUID.setVisibility(View.GONE);

        TextView textViewPeringatan = uuidDialog.findViewById(R.id.peringatan);
        TextView textViewPeringatan2 = uuidDialog.findViewById(R.id.peringatan2);

        textViewPeringatan.setText(R.string.registered);
        textViewPeringatan.setVisibility(View.VISIBLE);
        textViewPeringatan2.setVisibility(View.GONE);

        CardView buttonKeluar = uuidDialog.findViewById(R.id.uuid_button_keluar);
        CardView buttonLogin = uuidDialog.findViewById(R.id.uuid_button_login);
        buttonKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uuidDialog.dismiss();
                finish();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uuidDialog.dismiss();
                sendUserToLoginActivity();
            }
        });
        uuidDialog.show();
    }

    private void showDialogUUID(String namaPemilik){
        final Dialog uuidDialog = new Dialog(this);
        uuidDialog.setContentView(R.layout.wrong_uuid_layout);
        uuidDialog.setCanceledOnTouchOutside(false);
        TextView textViewNamaUUID = uuidDialog.findViewById(R.id.uuid_nama_server);
        textViewNamaUUID.setText(namaPemilik);

        TextView textViewPeringatan = uuidDialog.findViewById(R.id.peringatan);
        TextView textViewPeringatan2 = uuidDialog.findViewById(R.id.peringatan2);
        textViewPeringatan.setVisibility(View.VISIBLE);
        textViewPeringatan2.setVisibility(View.VISIBLE);

        CardView buttonKeluar = uuidDialog.findViewById(R.id.uuid_button_keluar);
        CardView buttonLogin = uuidDialog.findViewById(R.id.uuid_button_login);
        buttonKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uuidDialog.dismiss();
                finish();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uuidDialog.dismiss();
                sendUserToLoginActivity();
            }
        });
        uuidDialog.show();
    }

    private void setObjectDisplay() {
        textViewNama.setText(nama);
        textViewJurusan.setText(nama_jurusan);
        userType();
        displaySuccess();
    }

    public void sendUserToLoginActivity() {
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

    private void sendUserToQRActivity() {
        Intent intentQR = new Intent(MainActivity.this, QRActivity.class);
        intentQR.putExtra("MATIKAN_BT", matikanBluetooth);
        intentQR.putExtra("NOMOR_INDUK", nomor_induk);
        startActivity(intentQR);
    }

    private void sendUserToPengumumanActivity() {
        Intent intentPengumuman = new Intent(MainActivity.this, PengumumanActivity.class);
        intentPengumuman.putExtra("NOMOR_INDUK", nomor_induk);
        startActivity(intentPengumuman);
    }

    private void sendUserToJadwalMahasiswaActivity() {
        Intent intentJadwal = new Intent(MainActivity.this, JadwalMahasiswaActivity.class);
        intentJadwal.putExtra("NOMOR_INDUK", nomor_induk);
        startActivity(intentJadwal);
    }

    private void menuMahasiswa(){
        linearLayoutMenu.setWeightSum(3);
        cardViewForum.setVisibility(View.VISIBLE);
        cardViewAbsen.setVisibility(View.VISIBLE);
        cardViewJadwal.setVisibility(View.VISIBLE);
        cardViewLapAbsen.setVisibility(View.GONE);
    }

    private void menuDosen(){
        linearLayoutMenu.setWeightSum(3);
        cardViewForum.setVisibility(View.VISIBLE);
        cardViewAbsen.setVisibility(View.VISIBLE);
        cardViewJadwal.setVisibility(View.GONE);
        cardViewLapAbsen.setVisibility(View.VISIBLE);
    }

    private void menuDekan(){
        linearLayoutMenu.setWeightSum(1);
        cardViewForum.setVisibility(View.GONE);
        cardViewAbsen.setVisibility(View.GONE);
        cardViewJadwal.setVisibility(View.GONE);
        cardViewLapAbsen.setVisibility(View.VISIBLE);
    }

    private void userType() {
        String typeUser = nomor_induk.substring(0, 1);
        switch (typeUser) {
            case "9":
                menuDosen();
                break;
            case "8":
                menuDekan();
                break;
            default:
                menuMahasiswa();
                break;
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
                } else if (mac_user.length() != 17){
                    sendUserToInputMacActivity();
                } else {
                    requestEnableBT();
                }
            }
        });

        cardViewJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToJadwalMahasiswaActivity();
            }
        });

        cardViewLapAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Laporan Absensi", Toast.LENGTH_SHORT).show();
            }
        });

        // PENGUMUMAN
        main_rl_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPengumumanActivity();
            }
        });
    }

    private void updateJadwalEveryDay() {
        if (nomor_induk != null){
            String typeUser = nomor_induk.substring(0,1);
            if (!typeUser.equals("8")){
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE,1);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
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
        }
    }
}
