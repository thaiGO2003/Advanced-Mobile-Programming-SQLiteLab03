package com.example.advanced_mobile_programming_lab03_sqlite.model;

public class UserModel {
    private int ID; // Thêm thuộc tính ID
    private String username;
    private String password;
    private String email;
    private String fullName;

    // Constructor (Hàm khởi tạo)
    public UserModel(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }

    public UserModel() {
    }

    // Getters và Setters (Phương thức truy xuất và thiết lập)
    public int getID() { return ID; } // Getter cho ID

    public void setID(int ID) { this.ID = ID; } // Setter cho ID

    // Getters và Setters (Phương thức truy xuất và thiết lập)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Phương thức khác (ví dụ: hiển thị thông tin người dùng)
    public void displayUserInfo() {
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Full Name: " + fullName);
    }
}