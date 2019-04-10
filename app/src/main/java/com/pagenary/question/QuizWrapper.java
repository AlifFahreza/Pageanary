package com.pagenary.question;

public class QuizWrapper {
    private String soal;
    private String id_kategory;
    private String id;
    private int urutan;

    public QuizWrapper(String soal, String id_kategory, String id, int urutan) {
        this.soal = soal;
        this.id_kategory = id_kategory;
        this.id = id;
        this.urutan = urutan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_kategory() {
        return id_kategory;
    }

    public void setId_kategory(String id_kategory) {
        this.id_kategory = id_kategory;
    }

    public String getSoal() {
        return soal;
    }

    public void setSoal(String soal) {
        this.soal = soal;
    }

    public int getUrutan() {
        return urutan;
    }

    public void setUrutan(int urutan) {
        this.urutan = urutan;
    }
}
