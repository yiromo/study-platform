package com.example.studyplatform.dto;

import com.example.studyplatform.model.UserType;

public class RegisterRequest {
    private String email;
    private String username;
    private String password;
    private UserType userType;

    public RegisterRequest() {
    }

    public RegisterRequest(String email, String username, String password, UserType userType) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}