package com.example.studyplatform.dto;

import java.util.UUID;

import com.example.studyplatform.model.UserTestGrade;

public class UserTestGradeResponseDto {
    private UUID id;
    private UUID courseId;
    private String courseTitle;
    private UUID userId;
    private String username;
    private Integer courseMaxTest;
    private Integer userGrade;
    
    public UserTestGradeResponseDto() {
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getCourseId() {
        return courseId;
    }
    
    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }
    
    public String getCourseTitle() {
        return courseTitle;
    }
    
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Integer getCourseMaxTest() {
        return courseMaxTest;
    }
    
    public void setCourseMaxTest(Integer courseMaxTest) {
        this.courseMaxTest = courseMaxTest;
    }
    
    public Integer getUserGrade() {
        return userGrade;
    }
    
    public void setUserGrade(Integer userGrade) {
        this.userGrade = userGrade;
    }
    
    public static UserTestGradeResponseDto fromEntity(UserTestGrade grade) {
        UserTestGradeResponseDto dto = new UserTestGradeResponseDto();
        dto.setId(grade.getId());
        dto.setCourseId(grade.getCourse().getId());
        dto.setCourseTitle(grade.getCourse().getTitle());
        dto.setUserId(grade.getUser().getId());
        dto.setUsername(grade.getUser().getUsername());
        dto.setCourseMaxTest(grade.getCourseMaxTest());
        dto.setUserGrade(grade.getUserGrade());
        return dto;
    }
}