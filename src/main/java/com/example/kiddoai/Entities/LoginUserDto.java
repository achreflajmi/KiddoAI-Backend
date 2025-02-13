package com.example.kiddoai.Entities;


public class LoginUserDto {
    private String email;

    private String password;

    // getters and setters here...

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
}