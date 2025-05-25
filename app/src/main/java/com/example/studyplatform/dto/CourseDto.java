package com.example.studyplatform.dto;

public class CourseDto {
    private String title;
    private String description;
    private String approximateTime;
    
    public CourseDto() {
    }
    
    public CourseDto(String title, String description, String approximateTime) {
        this.title = title;
        this.description = description;
        this.approximateTime = approximateTime;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getApproximateTime() {
        return approximateTime;
    }
    
    public void setApproximateTime(String approximateTime) {
        this.approximateTime = approximateTime;
    }
}