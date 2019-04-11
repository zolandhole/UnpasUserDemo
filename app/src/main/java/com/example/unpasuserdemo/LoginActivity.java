package com.example.unpasuserdemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unpasuserdemo.handlers.DBHandler;
import com.example.unpasuserdemo.models.ModelUser;
import com.example.unpasuserdemo.utils.ServerUnpas;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;

public class LoginActivity extends AppCompatActivity {

    private AppCompatEditText login_username, login_password;
    private Button login_button;
    private String username, password;
    private ProgressDialog progressDialog;
    private DBHandler dbHandler;
    private Dialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        runningActivity();
        onClick();
    }

    private void runningActivity() {
        progressDialog = new ProgressDialog(this);
        dbHandler = new DBHandler(this);
    }

    private void onClick() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(login_username.getText())){
                    Toast.makeText(LoginActivity.this, "Masukan nim anda", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(login_password.getText())){
                    Toast.makeText(LoginActivity.this, "Masukan password anda", Toast.LENGTH_SHORT).show();
                } else {
                    processForm();
                }
            }
        });
    }

    private void processForm() {
        username = Objects.requireNonNull(login_username.getText()).toString().trim();
        password = Objects.requireNonNull(login_password.getText()).toString().trim();
        displayLoading();
        ServerUnpas ambilData = new ServerUnpas(LoginActivity.this,"Login");
        synchronized (LoginActivity.this){ambilData.getData(username,password);}
    }

    private void initView() {
        login_username = findViewById(R.id.login_username);
        login_password = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
        noInternetDialog = new Dialog(this);
    }

    private void displayLoading() {
        progressDialog.setTitle("Mengambil data");
        progressDialog.setMessage("Mohon menunggu, sedang memuat data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void displaySuccess(){
        progressDialog.dismiss();
        noInternetDialog.dismiss();
    }

    public void insertUserToDB(String id_server, String nama, String nama_jurusan, String mac_user) {
        dbHandler.addUser(
                new ModelUser(1,id_server,username,password,nama, nama_jurusan,mac_user)
        );
        dbHandler.close();
        displaySuccess();
        sendUserToMainActivity();
    }

    private void sendUserToMainActivity() {
        Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentMain);
        finish();
    }

    public void showDialogNoInternet(){
        progressDialog.dismiss();
        noInternetDialog.setContentView(R.layout.wrong_uuid_layout);
        noInternetDialog.setCanceledOnTouchOutside(true);
        noInternetDialog.setCancelable(true);

        TextView textViewPeringatan = noInternetDialog.findViewById(R.id.peringatan);
        TextView textViewNamaUUID = noInternetDialog.findViewById(R.id.uuid_nama_server);
        TextView textViewPeringatan2 = noInternetDialog.findViewById(R.id.peringatan2);
        CardView buttonKeluar = noInternetDialog.findViewById(R.id.uuid_button_keluar);
        CardView buttonLogin = noInternetDialog.findViewById(R.id.uuid_button_login);
        TextView button_login_text = noInternetDialog.findViewById(R.id.button_login_text);

        textViewPeringatan.setText(R.string.kesalahan);
        textViewNamaUUID.setText(R.string.nointernet);
        textViewPeringatan2.setText(R.string.server_problem);
        button_login_text.setText(R.string.ulangi);

        textViewPeringatan.setVisibility(View.VISIBLE);
        textViewNamaUUID.setVisibility(View.VISIBLE);
        textViewPeringatan2.setVisibility(View.VISIBLE);
        buttonKeluar.setVisibility(View.INVISIBLE);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processForm();
                noInternetDialog.dismiss();
            }
        });
        noInternetDialog.show();
    }
}
