package com.example.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unpasuserdemo.utils.AESUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class QRActivity extends AppCompatActivity {

    private static final String TAG = "QRActivity";
    private TextView qr_generate;
    private ImageView qr_image;
    private Button qr_selesai_button;
    private String nomor_induk;
    private SimpleDateFormat simpleDateFormat;
    private final static int QrWidth = 500;
    private final static int QrHeight = 500;
    private BluetoothAdapter bluetoothAdapter;
    private String matikanBLuetooth, textButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        initView();
        initListener();
        initRunning();
        enableBT();
        qr_selesai_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimUserKeMainActiviy();
            }
        });
    }

    private void enableBT() {
        if (bluetoothAdapter == null){
            Log.e(TAG, "enableBT: Perangkat tidak mendukung bluetooth");
        }

        if (bluetoothAdapter.isEnabled()){
            Intent intentBTEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intentBTEnable);

            IntentFilter intentFilterBT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiverBTEnable, intentFilterBT);

            discoverBT();
        }
    }

    private void discoverBT() {
        if(!bluetoothAdapter.isDiscovering()){
            Intent intentDiscover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intentDiscover.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 240);
            startActivity(intentDiscover);

            IntentFilter intentFilterDis = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(receiverBTDiscoverable, intentFilterDis);
        } else if (bluetoothAdapter.cancelDiscovery()){
            Log.e(TAG, "discoverBT: DENIED");
        }
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
                        kirimUserKeMainActiviy();
                        Toast.makeText(QRActivity.this, "Untuk kegiatan ini anda perlu menyalakan bluetooth", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.e(TAG, "onReceive: TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.e(TAG, "onReceive: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.e(TAG, "onReceive: TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver receiverBTDiscoverable = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                switch (mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.e(TAG, "onReceive: Discoverability Disable, but can receive connection");
                        qr_selesai_button.setText(R.string.selesai);
                        qr_image.setVisibility(View.VISIBLE);
                        initRunning();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.e(TAG, "onReceive: Connectale");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.e(TAG, "onReceive: Not Discover");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.e(TAG, "onReceive: connecting");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.e(TAG, "onReceive: connected");
                        break;

                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (matikanBLuetooth.equals("ya")){
            turnOffBT();
        }
    }

    private void turnOffBT() {
        if (bluetoothAdapter.isEnabled()){
            bluetoothAdapter.disable();
            IntentFilter intentFilterBT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiverBTEnable, intentFilterBT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (matikanBLuetooth.equals("ya")){
            turnOffBT();
        }
        try{
            unregisterReceiver(receiverBTEnable);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverBTEnable);
            unregisterReceiver(receiverBTDiscoverable);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverBTDiscoverable);
        } catch (Exception e){
            Log.e(TAG, "onPause: ");
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void initListener() {
        nomor_induk = Objects.requireNonNull(getIntent().getExtras()).getString("NOMOR_INDUK");
        matikanBLuetooth = getIntent().getExtras().getString("MATIKAN_BT");
        Log.e(TAG, "initListener: "+ matikanBLuetooth);
        simpleDateFormat = new SimpleDateFormat("mm:ss");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void kirimUserKeMainActiviy() {
        Intent intentMain = new Intent(QRActivity.this, MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentMain);
        finish();
    }

    private void initView() {
        qr_generate = findViewById(R.id.qr_generate);
        qr_image = findViewById(R.id.qr_image);
        qr_selesai_button = findViewById(R.id.qr_selesai_button);
    }

    private void initRunning() {
        textButton = qr_selesai_button.getText().toString();
        Log.e(TAG, "initRunning: "+ textButton);
        if (textButton.equals("Selesai")){
            generateQR();
        }
    }

    private void generateQR() {

        Calendar dateTimeKalender = Calendar.getInstance();
        Date date = dateTimeKalender.getTime();
        String dateFormat = simpleDateFormat.format(date);
        String sourceString = nomor_induk + " " + dateFormat;
        try {
            String encripted = AESUtils.encrypt(sourceString);
            qr_image.setImageBitmap(TextToImageEncode(encripted));
            qr_image.setVisibility(View.VISIBLE);
            countingDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap TextToImageEncode(String encripted) throws WriterException {
        BitMatrix bitMatrix;
        bitMatrix = new MultiFormatWriter().encode(
                encripted, BarcodeFormat.QR_CODE, QrWidth, QrHeight, null
        );
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        int warnaQR = getResources().getColor(R.color.colorPrimary);
        int backgroundQR = getResources().getColor(R.color.bodas);
        for (int y=0; y<bitMatrixHeight; y++){
            int offset = y * bitMatrixWidth;
            for (int x=0; x<bitMatrixWidth; x++){
                pixels[offset + x] = bitMatrix.get(x,y) ? warnaQR:backgroundQR;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels,0,500,0,0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private void countingDown(){
        CountDownTimer countDownTimer = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished / 1000 == 0) {
//                    qr_image.setVisibility(View.GONE);
                    generateQR();
                } else {
                    qr_generate.setText(String.valueOf(millisUntilFinished / 1000));
                }
            }
            @Override
            public void onFinish() {
                if (textButton.equals("")){
                    kirimUserKeMainActiviy();
                }
            }
        };
        countDownTimer.start();
    }
}
