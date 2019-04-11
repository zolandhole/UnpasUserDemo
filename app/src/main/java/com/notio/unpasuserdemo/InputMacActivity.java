package com.notio.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.notio.unpasuserdemo.handlers.DBHandler;
import com.notio.unpasuserdemo.models.ModelUser;
import com.notio.unpasuserdemo.utils.ServerUnpas;
import com.phearme.macaddressedittext.MacAddressEditText;

import java.util.Objects;

public class InputMacActivity extends AppCompatActivity {

//    private String TAG = "InputMacActivity";
    private MacAddressEditText mac_input;
    private Button mac_button;
    private String id_user, nomor_induk, password;
    private ProgressDialog progressDialog;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_mac);

        initView();
        initListener();

        mac_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMacAddToServer();
            }
        });
    }

    private void sendMacAddToServer() {
        displayLoading();
        if (TextUtils.isEmpty(mac_input.getText())){
            Toast.makeText(this, "Masukan MAC ADDRESS Bletooth anda", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (Objects.requireNonNull(mac_input.getText()).toString().trim().length() != 17){
            Toast.makeText(this, "Periksa kembali Mac Address Bluetooth anda", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {
            ServerUnpas kirimMac = new ServerUnpas(InputMacActivity.this, "kirimMac");
            synchronized (InputMacActivity.this) {kirimMac.sendMacAddress(id_user,nomor_induk,mac_input.getText().toString().trim());}
        }
    }

    private void initListener() {
        progressDialog = new ProgressDialog(this);
        dbHandler = new DBHandler(this);
        id_user = Objects.requireNonNull(getIntent().getExtras()).getString("ID_USER");
        nomor_induk = getIntent().getExtras().getString("NOMOR_INDUK");
        password = getIntent().getExtras().getString("PASSWORD");
    }

    private void initView() {
        mac_input = findViewById(R.id.mac_input);
        mac_button = findViewById(R.id.mac_button);
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

    public void processResult(String status) {
        if (status.equals("success")){
            saveMacToDB();
        }
        displaySuccess();
    }

    private void saveMacToDB() {
        ServerUnpas updateData = new ServerUnpas(InputMacActivity.this, "updateMac");
        synchronized (InputMacActivity.this){
            updateData.getData(nomor_induk, password);
        }
    }

    public void updateUserToDB(String id_server, String nama, String nama_jurusan, String mac_user) {
        dbHandler.deleteAll();
        dbHandler.addUser(
                new ModelUser(1,id_server, nomor_induk,password,nama, nama_jurusan, mac_user)
        );
        dbHandler.close();
        sendUserToQRActivity();
        displaySuccess();
    }

    private void sendUserToQRActivity() {
        Intent intentMain = new Intent(InputMacActivity.this, MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentMain);
        finish();
    }
}
