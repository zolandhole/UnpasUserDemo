package com.example.unpasuserdemo.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.unpasuserdemo.LoginActivity;
import com.example.unpasuserdemo.MainActivity;
import com.example.unpasuserdemo.models.ModelUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class ServerUnpas {

    private Context context;
    private String aktifitas;

    public ServerUnpas(Context context, String aktifitas){
        this.context = context;
        this.aktifitas = aktifitas;
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
                                            dataUser.setNomor_induk(dataServer.getString("nomor_induk"));
                                            dataUser.setNama(dataServer.getString("nama"));
                                            dataUser.setNama_jurusan(dataServer.getString("nama_jurusan"));
                                            dataUser.setStatus(dataServer.getString("status"));
                                            dataUser.setMac_user(dataServer.getString("mac_user"));
                                        }
                                        loginActivity.insertUserToDB(dataUser.getNama(),dataUser.getNama_jurusan(),dataUser.getMac_user());
                                    }
                                    break;
                                case "syncData":
                                    MainActivity mainActivity = (MainActivity) context;
                                    if (jsonObject.optString("error").equals("true")){
                                        String errorMessage = jsonObject.getString("message");
                                        mainActivity.displaySuccess();
                                        Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_LONG).show();
                                    } else if (jsonObject.optString("error").equals("false")){
                                        JSONArray jsonArray = jsonObject.getJSONArray("message");
                                        ModelUser dataUser = new ModelUser();
                                        for (int i=0; i < jsonArray.length(); i++){
                                            JSONObject dataServer = jsonArray.getJSONObject(i);
                                            dataUser.setNomor_induk(dataServer.getString("nomor_induk"));
                                            dataUser.setNama(dataServer.getString("nama"));
                                            dataUser.setNama_jurusan(dataServer.getString("nama_jurusan"));
                                            dataUser.setStatus(dataServer.getString("status"));
                                            dataUser.setMac_user(dataServer.getString("mac_user"));
                                            dataUser.setPassword(dataServer.getString("password"));
                                        }
                                        mainActivity.dataUserFromServer(dataUser.getNama(),dataUser.getNama_jurusan(),dataUser.getMac_user(),dataUser.getPassword());
                                    }
                                    break;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: exception"+ e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: "+ error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("nomor_induk", username);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
