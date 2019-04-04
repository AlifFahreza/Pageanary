package com.pagenary.question;

public class Product {
    private String id_kategory;
    private String gambar;
    private String nama;

    public Product(String id_kategory, String gambar, String nama) {
        this.id_kategory = id_kategory;
        this.gambar = gambar;
        this.nama = nama;
    }

    public String getId_kategory() {
        return id_kategory;
    }

    public String getGambar() {
        return gambar;
    }

    public String getNama() {
        return nama;
    }
}