package com.example.studyplatform.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studyplatform.model.Course;
import com.example.studyplatform.model.User;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByUploadedUser(User user);
    boolean existsByIdAndUploadedUser(UUID id, User user);
}