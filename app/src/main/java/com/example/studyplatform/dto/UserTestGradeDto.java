package com.example.studyplatform.dto;

public class UserTestGradeDto {
    private Integer courseMaxTest;
    private Integer userGrade;
    
    public UserTestGradeDto() {
    }
    
    public UserTestGradeDto(Integer courseMaxTest, Integer userGrade) {
        this.courseMaxTest = courseMaxTest;
        this.userGrade = userGrade;
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
}