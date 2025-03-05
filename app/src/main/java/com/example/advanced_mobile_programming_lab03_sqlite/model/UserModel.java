package com.example.advanced_mobile_programming_lab03_sqlite.model;

public class UserModel {
    private int ID; // Thêm thuộc tính ID
    private String username;
    private String password;
    private String email;
    private String fullName;
    private byte[] imageBlob;

    public UserModel(int id, String username, String password, String email, String fullName, byte[] imageBlob) {
        this.ID = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.imageBlob = imageBlob;
    }

    public int getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public byte[] getImageBlob() {
        return imageBlob;
    }
}