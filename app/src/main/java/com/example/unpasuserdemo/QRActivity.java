package com.example.unpasuserdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private ProgressDialog progressDialog;
    private CountDownTimer countDownTimer;
    private ProgressBar qr_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        initView();
        initListener();
//        displayLoading();
        qr_selesai_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimUserKeMainActiviy();
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void initListener() {
        nomor_induk = Objects.requireNonNull(getIntent().getExtras()).getString("NOMOR_INDUK");
        simpleDateFormat = new SimpleDateFormat("mm:ss");
        progressDialog = new ProgressDialog(this);
    }

    private void displayLoading(){
        progressDialog.setTitle("Mengambil data");
        progressDialog.setMessage("Mohon menunggu, sedang memuat data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void displaySuccess(){
        progressDialog.dismiss();
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
        qr_progressBar = findViewById(R.id.qr_progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRunning();
    }

    private void initRunning() {
        generateQR();
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
        countDownTimer = new CountDownTimer(11000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished/1000 == 0){
                    qr_generate.setText(R.string.memperbaharui_qr_core);
                    qr_image.setVisibility(View.GONE);
                    qr_progressBar.setVisibility(View.GONE);
                    generateQR();
                } else {
                    qr_generate.setText(String.valueOf(millisUntilFinished/1000));
                    qr_image.setVisibility(View.VISIBLE);
                    qr_progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

}
