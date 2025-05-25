package com.example.studyplatform.dto;

import com.example.studyplatform.model.ModuleType;

public class ModuleDto {
    private String moduleTitle;
    private ModuleType moduleType;
    private String text;
    private String videoId;
    private String testId;
    
    public ModuleDto() {
    }
    
    public String getModuleTitle() {
        return moduleTitle;
    }
    
    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }
    
    public ModuleType getModuleType() {
        return moduleType;
    }
    
    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getVideoId() {
        return videoId;
    }
    
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    
    public String getTestId() {
        return testId;
    }
    
    public void setTestId(String testId) {
        this.testId = testId;
    }
}