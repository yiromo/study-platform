package com.example.studyplatform.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "uploaded_user_id", nullable = false)
    private User uploadedUser;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "approximate_time")
    private String approximateTime;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    public Course() {
    }
    
    public Course(User uploadedUser, String title, String description, String approximateTime) {
        this.uploadedUser = uploadedUser;
        this.title = title;
        this.description = description;
        this.approximateTime = approximateTime;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public User getUploadedUser() {
        return uploadedUser;
    }
    
    public void setUploadedUser(User uploadedUser) {
        this.uploadedUser = uploadedUser;
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
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}