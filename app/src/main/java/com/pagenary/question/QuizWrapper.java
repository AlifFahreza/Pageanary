package com.pagenary.question;

public class QuizWrapper {
    private String soal;
    private String id_kategory;

    public QuizWrapper(String soal, String id_kategory) {
        this.soal = soal;
        this.id_kategory = id_kategory;
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
}
