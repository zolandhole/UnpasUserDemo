package com.example.unpasuserdemo.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.unpasuserdemo.models.ModelUser;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Userinfo";
    private static final String TABLE_USER = "user";

    private static final String KEY_ID = "id";
    private static final String KEY_NOMOR_INDUK = "nomor_induk";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_NAMA = "nama";
    private static final String KEY_NAMA_JURUSAN = "nama_jurusan";
    private static final String KEY_MAC_USER = "mac_user";

    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NOMOR_INDUK + " TEXT, "
                + KEY_PASSWORD + " TEXT, "
                + KEY_NAMA + " TEXT, "
                + KEY_NAMA_JURUSAN + " TEXT, "
                + KEY_MAC_USER + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void addUser(ModelUser user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values =new ContentValues();
        values.put(KEY_ID, user.getId());
        values.put(KEY_NOMOR_INDUK, user.getNomor_induk());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_NAMA, user.getNama());
        values.put(KEY_NAMA_JURUSAN, user.getNama_jurusan());
        values.put(KEY_MAC_USER, user.getMac_user());
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public ArrayList<HashMap<String, String>> getUser(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT * FROM "+ TABLE_USER;
        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID, KEY_NOMOR_INDUK, KEY_PASSWORD, KEY_NAMA, KEY_NAMA_JURUSAN, KEY_MAC_USER},
                KEY_ID+ "=?",new String[]{String.valueOf(userid)},null, null, null, null);
        if (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("id",cursor.getString(cursor.getColumnIndex(KEY_ID)));
            user.put("nomor_induk",cursor.getString(cursor.getColumnIndex(KEY_NOMOR_INDUK)));
            user.put("password",cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            user.put("nama",cursor.getString(cursor.getColumnIndex(KEY_NAMA)));
            user.put("nama_jurusan",cursor.getString(cursor.getColumnIndex(KEY_NAMA_JURUSAN)));
            user.put("mac_user",cursor.getString(cursor.getColumnIndex(KEY_MAC_USER)));
            userList.add(user);
        }
        return  userList;
    }

    public void  deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.execSQL("DELETE FROM " + TABLE_USER);
        db.close();
    }
}
