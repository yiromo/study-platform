package com.example.studyplatform.model;

import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "modules")
public class CourseModule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "module_type", nullable = false)
    private ModuleType moduleType;
    
    @NotNull
    @Min(1)
    @Column(name = "module_num", nullable = false)
    private Integer moduleNum;
    
    @NotNull
    @Column(name = "module_title", nullable = false)
    private String moduleTitle;
    
    @Column(columnDefinition = "TEXT")
    private String text;
    
    @ManyToOne
    @JoinColumn(name = "video_id")
    private ModuleVideo video;
    
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
    
    public CourseModule() {
    }
    
    public CourseModule(Course course, ModuleType moduleType, Integer moduleNum, String moduleTitle) {
        this.course = course;
        this.moduleType = moduleType;
        this.moduleNum = moduleNum;
        this.moduleTitle = moduleTitle;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }
    
    public ModuleType getModuleType() {
        return moduleType;
    }
    
    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }
    
    public Integer getModuleNum() {
        return moduleNum;
    }
    
    public void setModuleNum(Integer moduleNum) {
        this.moduleNum = moduleNum;
    }
    
    public String getModuleTitle() {
        return moduleTitle;
    }
    
    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public ModuleVideo getVideo() {
        return video;
    }
    
    public void setVideo(ModuleVideo video) {
        this.video = video;
    }
    
    public Test getTest() {
        return test;
    }
    
    public void setTest(Test test) {
        this.test = test;
    }
}