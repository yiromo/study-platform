package com.example.studyplatform.model;

import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_test_grades")
public class UserTestGrade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "course_max_test", nullable = false)
    private Integer courseMaxTest;
    
    @Column(name = "user_grade", nullable = false)
    private Integer userGrade;
    
    public UserTestGrade() {
    }
    
    public UserTestGrade(Course course, User user, Integer courseMaxTest, Integer userGrade) {
        this.course = course;
        this.user = user;
        this.courseMaxTest = courseMaxTest;
        this.userGrade = userGrade;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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