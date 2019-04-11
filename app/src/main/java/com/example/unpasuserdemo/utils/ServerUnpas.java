package com.example.unpasuserdemo.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.unpasuserdemo.InputMacActivity;
import com.example.unpasuserdemo.JadwalMahasiswaActivity;
import com.example.unpasuserdemo.LoginActivity;
import com.example.unpasuserdemo.MainActivity;
import com.example.unpasuserdemo.models.ModelJadwal;
import com.example.unpasuserdemo.models.ModelUser;
import com.example.unpasuserdemo.services.FeedService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class ServerUnpas {

    private Context context;
    private String aktifitas, status;
    private List<ModelJadwal> listJadwal;
    private List<String> jamJadwal, jamMatakuliah;

    public ServerUnpas(Context context, String aktifitas){
        this.context = context;
        this.aktifitas = aktifitas;
    }

    public void sendMacAddress (final String id_user, final String nomor_induk, final String mac_user){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerSide.POST_MACADD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            InputMacActivity inputMacActivity = (InputMacActivity) context;
                            String message = jsonObject.getString("message");
                            Toast.makeText(inputMacActivity, message, Toast.LENGTH_SHORT).show();
                            if (jsonObject.optString("error").equals("true")){
                                status = "failed";
                            } else if (jsonObject.optString("error").equals("false")){
                                status = "success";
                            }
                            inputMacActivity.processResult(status);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: "+ error);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("id_user", id_user);
                params.put("nomor_induk", nomor_induk);
                params.put("mac_user", mac_user);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void sendUUID (final String id_user, final String nomor_induk, final String uuid){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerSide.POST_UUID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MainActivity mainActivity = (MainActivity) context;
                        String namaPemilik = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("error").equals("true")){
                                Log.e(TAG, "onResponse: sendUUID " + jsonObject.getJSONArray("message"));
                            } else if (jsonObject.optString("error").equals("false")){
                                JSONArray jsonArray = jsonObject.getJSONArray("message");
                                for (int i=0; i < jsonArray.length(); i++){
                                    JSONObject dataServer = jsonArray.getJSONObject(i);
                                    namaPemilik = dataServer.getString("nama_pemilik");
                                }
                            }
                            mainActivity.resultUpdateUUID(namaPemilik);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: exception "+ e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: "+ error);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("id_user", id_user);
                params.put("nomor_induk", nomor_induk);
                params.put("uuid", uuid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void getData (final String username, final String password){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerSide.POST_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (aktifitas){
                                case "Login":
                                    LoginActivity loginActivity = (LoginActivity) context;
                                    if (jsonObject.optString("error").equals("true")){
                                        String errorMessage = jsonObject.getString("message");
                                        loginActivity.displaySuccess();
                                        Toast.makeText(loginActivity, errorMessage, Toast.LENGTH_LONG).show();
                                    } else if (jsonObject.optString("error").equals("false")){
                                        JSONArray jsonArray = jsonObject.getJSONArray("message");
                                        ModelUser dataUser = new ModelUser();
                                        for (int i=0; i < jsonArray.length(); i++){
                                            JSONObject dataServer = jsonArray.getJSONObject(i);
                                            dataUser.setId_server(dataServer.getString("id"));
                                            dataUser.setNomor_induk(dataServer.getString("nomor_induk"));
                                            dataUser.setNama(dataServer.getString("nama"));
                                            dataUser.setNama_jurusan(dataServer.getString("nama_jurusan"));
                                            dataUser.setStatus(dataServer.getString("status"));
                                            dataUser.setMac_user(dataServer.getString("mac_user"));
                                        }
                                        loginActivity.insertUserToDB(dataUser.getId_server(),dataUser.getNama(),dataUser.getNama_jurusan(),dataUser.getMac_user());
                                    }
                                    break;
                                case "syncData":
                                    MainActivity mainActivity = (MainActivity) context;
                                    if (jsonObject.optString("error").equals("true")){
                                        String errorMessage = jsonObject.getString("message");
                                        mainActivity.displaySuccess();
                                        Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_LONG).show();
                                        mainActivity.sendUserToLoginActivity();
                                    } else if (jsonObject.optString("error").equals("false")){
                                        JSONArray jsonArray = jsonObject.getJSONArray("message");
                                        ModelUser dataUser = new ModelUser();
                                        String uuidServer = "";
                                        for (int i=0; i < jsonArray.length(); i++){
                                            JSONObject dataServer = jsonArray.getJSONObject(i);
                                            dataUser.setId_server(dataServer.getString("id"));
                                            dataUser.setNomor_induk(dataServer.getString("nomor_induk"));
                                            dataUser.setNama(dataServer.getString("nama"));
                                            dataUser.setNama_jurusan(dataServer.getString("nama_jurusan"));
                                            dataUser.setStatus(dataServer.getString("status"));
                                            dataUser.setMac_user(dataServer.getString("mac_user"));
                                            dataUser.setPassword(dataServer.getString("password"));
                                            uuidServer = dataServer.getString("uuid");
                                        }
                                        mainActivity.dataUserFromServer(
                                                dataUser.getId_server(),dataUser.getNama(),dataUser.getNama_jurusan(),
                                                dataUser.getMac_user(),dataUser.getPassword(), uuidServer
                                        );
                                    }
                                    break;
                                case "updateMac":
                                    InputMacActivity inputMacActivity = (InputMacActivity) context;
                                    if (jsonObject.optString("error").equals("true")){
                                        String errorMessage = jsonObject.getString("message");
                                        inputMacActivity.displaySuccess();
                                        Toast.makeText(inputMacActivity, errorMessage, Toast.LENGTH_LONG).show();
                                    } else if (jsonObject.optString("error").equals("false")){
                                        JSONArray jsonArray = jsonObject.getJSONArray("message");
                                        ModelUser dataUser = new ModelUser();
                                        for (int i=0; i < jsonArray.length(); i++){
                                            JSONObject dataServer = jsonArray.getJSONObject(i);
                                            dataUser.setId_server(dataServer.getString("id"));
                                            dataUser.setNomor_induk(dataServer.getString("nomor_induk"));
                                            dataUser.setNama(dataServer.getString("nama"));
                                            dataUser.setNama_jurusan(dataServer.getString("nama_jurusan"));
                                            dataUser.setStatus(dataServer.getString("status"));
                                            dataUser.setMac_user(dataServer.getString("mac_user"));
                                        }
                                        inputMacActivity.updateUserToDB(dataUser.getId_server(),dataUser.getNama(),dataUser.getNama_jurusan(),dataUser.getMac_user());
                                    }
                                    break;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: exception"+ e);
                            switch (aktifitas){
                                case "Login":
                                    LoginActivity loginActivity = (LoginActivity) context;
                                    loginActivity.showDialogNoInternet();
                                    break;
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: "+ error);
                        switch (aktifitas){
                            case "Login":
                                LoginActivity loginActivity = (LoginActivity) context;
                                loginActivity.showDialogNoInternet();
                                break;
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params =new HashMap<>();
                params.put("nomor_induk", username);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void getNamaUUID(final String uuidServer) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerSide.GET_NAMAUUID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MainActivity mainActivity = (MainActivity) context;
                        String namaUserDiServer = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("error").equals("true")){
                                String message = jsonObject.getString("message");
                                Log.e(TAG, "onResponse: error True" + message);
                            } else if (jsonObject.optString("error").equals("false")){
                                JSONArray jsonArray = jsonObject.getJSONArray("message");
                                for (int i=0; i < jsonArray.length(); i++){
                                    JSONObject dataServer = jsonArray.getJSONObject(i);
                                    namaUserDiServer = dataServer.getString("nama");

                                }
                                mainActivity.resultNamaUUIDfromServer(namaUserDiServer);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("uuid", uuidServer);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void getDataJadwal(final String nomor_induk){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerSide.GET_JADWAL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("error").equals("true")){
                                Log.e(TAG, "onResponse: Error True" + jsonObject.getString("message"));
                            } else {
                                JSONArray jsonArray = jsonObject.getJSONArray("message");
                                switch (aktifitas){
                                    case "getJadwalKuliah":
                                        JadwalMahasiswaActivity jadwalMahasiswaActivity = (JadwalMahasiswaActivity) context;
                                        listJadwal = new ArrayList<>();
                                        for (int i=0; i<jsonArray.length(); i++){
                                            JSONObject dataServer = jsonArray.getJSONObject(i);
                                            ModelJadwal modelJadwal = new ModelJadwal();
                                            modelJadwal.setNim(dataServer.getString("nim"));
                                            modelJadwal.setNama(dataServer.getString("nama"));
                                            modelJadwal.setNama_jurusan(dataServer.getString("nama_jurusan"));
                                            modelJadwal.setNama_fakultas(dataServer.getString("nama_fakultas"));
                                            modelJadwal.setNama_matakuliah(dataServer.getString("nama_matakuliah"));
                                            modelJadwal.setNama_dosen(dataServer.getString("nama_dosen"));
                                            modelJadwal.setJam_mulai(dataServer.getString("jam_mulai"));
                                            modelJadwal.setJam_selesai(dataServer.getString("jam_selesai"));
                                            modelJadwal.setNama_ruangan(dataServer.getString("nama_ruangan"));
                                            listJadwal.add(modelJadwal);
                                        }
                                        jadwalMahasiswaActivity.resultGetJadwal(listJadwal);
                                        break;
                                    case "GetDataForService":
                                        FeedService feedService = (FeedService) context;
                                        jamJadwal = new ArrayList<>();
                                        jamMatakuliah = new ArrayList<>();
                                        for (int i=0; i<jsonArray.length(); i++){
                                            JSONObject dataServer = jsonArray.getJSONObject(i);
                                            jamMatakuliah.add(dataServer.getString("nama_matakuliah"));
                                            jamJadwal.add(dataServer.getString("jam_mulai"));
                                        }
                                        feedService.resultGetDataForService(jamJadwal, jamMatakuliah);
                                        break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: Exception" + e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: getDataJadwal" + error);
                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("nomor_induk", nomor_induk);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
