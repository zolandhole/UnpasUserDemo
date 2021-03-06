package com.notio.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.notio.unpasuserdemo.adapters.AdapterPengumuman;
import com.notio.unpasuserdemo.handlers.DBHandler;
import com.notio.unpasuserdemo.models.ModelPengumuman;
import com.notio.unpasuserdemo.models.ModelUser;
import com.notio.unpasuserdemo.services.FeedService;
import com.notio.unpasuserdemo.utils.ServerUnpas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    private String TAG = "MainActivity";
    private static String url_profile;
    private TextView textViewNama, textViewJurusan;
    private CardView cardViewForum, cardViewAbsen, cardViewJadwal, cardViewLapAbsen;
    private LinearLayout linearLayoutMenu;
    private RelativeLayout main_rl_tambah;
    private ProgressDialog progressDialog;
    private DBHandler dbHandler;
    private String idUser, nomor_induk, nama, nama_jurusan, mac_user, password, uuid, serverUUID, matikanBluetooth, token;
    private BluetoothAdapter bluetoothAdapter;
    private RecyclerView recyclerViewPengumuman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbarBusinness();
        initView();
        initListener();

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                Log.e(TAG, "onSuccess: " + token);
            }
        });

        onClick();
        initRunning();

    }

    private void getImageUrl() {
        ServerUnpas serverUnpas = new ServerUnpas(this, "get_image");
        synchronized (this){
            serverUnpas.sendSingleString(nomor_induk);
        }
    }
    public static void resultgetImageProfile(String image_url) {
        url_profile = image_url;
    }


    private void checkUpdateVersion() {
        ServerUnpas serverUnpas = new ServerUnpas(MainActivity.this, "CheckUpdateVersion");
        synchronized (MainActivity.this){
            serverUnpas.getVersion();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUpdateVersion();
        Intent intent = new Intent(MainActivity.this, FeedService.class);
        startActivity(intent);
        getPengumuman();
        updatePengumuman();
    }

    private void updatePengumuman() {
        IntentFilter intentFilter = new IntentFilter("YADIRUDIYANSAH");
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, intentFilter);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getPengumuman();
            Log.e(TAG, "onReceive: NOTIFIKASI");
        }
    };

    private void getPengumuman() {
        ServerUnpas serverUnpas = new ServerUnpas(MainActivity.this, "getPengumuman");
        synchronized (MainActivity.this) {
            serverUnpas.sendSingleString(nomor_induk);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getImageUrl();
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    public void resultGetPengumuman(List<ModelPengumuman> listPengumuman) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerViewPengumuman.setLayoutManager(manager);
        recyclerViewPengumuman.setHasFixedSize(true);
        RecyclerView.Adapter adapterPengumuman = new AdapterPengumuman(listPengumuman, nama);
        recyclerViewPengumuman.setAdapter(adapterPengumuman);
        recyclerViewPengumuman.setNestedScrollingEnabled(false);
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
        dbHandler.close();
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
        switch (item.getItemId()){
            case R.id.main_profile:
                sendUserToProfileActivity();
                break;
            case R.id.main_logout:
                sendUserToLoginActivity();
                break;
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

        recyclerViewPengumuman = findViewById(R.id.recyclerviewPengumuman);
    }

    private void initListener() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void initRunning() {
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
        uuid = "35" +
                Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits
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
        saveToken();
        userType();
        displaySuccess();
    }

    private void saveToken() {
        ServerUnpas serverUnpas = new ServerUnpas(MainActivity.this, "saveToken");
        synchronized (MainActivity.this){
            serverUnpas.saveToken(token, nomor_induk);
        }
    }

    public void sendUserToLoginActivity() {
        dbHandler.deleteAll();
        Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
        intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentLogin);
        finish();
    }

    private void sendUserToProfileActivity() {
        Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
        intentProfile.putExtra("NOMOR_INDUK", nomor_induk);
        intentProfile.putExtra("URL_PROFILE", url_profile);
        startActivity(intentProfile);
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
        intentPengumuman.putExtra("NAMA", nama);
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
        main_rl_tambah.setVisibility(View.GONE);
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

    public void resultGetVersion(String is_update, String version_update, String url_update) {
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        Log.e(TAG, "resultGetVersion: " + versionCode + " " + versionName + " " + version_update);
        if (is_update.equals("1")){
            if (!versionName.equals(version_update)){
                onUpdateCheckListener(version_update, url_update);
            }
        }
    }

    private void onUpdateCheckListener(String version_update, final String url_update) {
        final Dialog uuidDialog = new Dialog(this);
        uuidDialog.setContentView(R.layout.wrong_uuid_layout);
        uuidDialog.setCanceledOnTouchOutside(false);
        uuidDialog.setCancelable(false);
        TextView textViewNamaUUID = uuidDialog.findViewById(R.id.uuid_nama_server);
        textViewNamaUUID.setText(version_update);
        textViewNamaUUID.setVisibility(View.VISIBLE);

        TextView textViewPeringatan = uuidDialog.findViewById(R.id.peringatan);
        TextView textViewPeringatan2 = uuidDialog.findViewById(R.id.peringatan2);

        textViewPeringatan.setText(R.string.updateApp);
        textViewPeringatan.setVisibility(View.VISIBLE);
        textViewPeringatan2.setVisibility(View.GONE);

        TextView button_login_text = uuidDialog.findViewById(R.id.button_login_text);
        TextView button_keluar_text = uuidDialog.findViewById(R.id.button_keluar_text);
        button_login_text.setText(R.string.update);
        button_keluar_text.setText(R.string.keluar);

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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url_update)));
            }
        });
        uuidDialog.show();
    }
}
