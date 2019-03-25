package com.example.unpasuserdemo.models;

public class ModelUser {
    private int id;
    private String id_server, nomor_induk, password, nama, nama_jurusan, status, mac_user;

    public ModelUser() {
    }

    public ModelUser(int id, String id_server, String nomor_induk, String password, String nama,String nama_jurusan, String mac_user) {
        this.id = id;
        this.id_server = id_server;
        this.nomor_induk = nomor_induk;
        this.password = password;
        this.nama = nama;
        this.nama_jurusan = nama_jurusan;
        this.mac_user = mac_user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId_server() {
        return id_server;
    }

    public void setId_server(String id_server) {
        this.id_server = id_server;
    }

    public String getNomor_induk() {
        return nomor_induk;
    }

    public void setNomor_induk(String nomor_induk) {
        this.nomor_induk = nomor_induk;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama_jurusan() {
        return nama_jurusan;
    }

    public void setNama_jurusan(String nama_jurusan) {
        this.nama_jurusan = nama_jurusan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMac_user() {
        return mac_user;
    }

    public void setMac_user(String mac_user) {
        this.mac_user = mac_user;
    }
}

