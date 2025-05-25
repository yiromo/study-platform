package com.example.studyplatform.dto;

import java.util.UUID;

import com.example.studyplatform.model.User;
import com.example.studyplatform.model.UserType;

public class UserResponseDto {
    private UUID id;
    private String email;
    private String username;
    private UserType userType;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public static UserResponseDto fromEntity(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setUserType(user.getUserType());
        return dto;
    }
}