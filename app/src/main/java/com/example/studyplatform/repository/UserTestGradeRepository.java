package com.example.studyplatform.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studyplatform.model.Course;
import com.example.studyplatform.model.User;
import com.example.studyplatform.model.UserTestGrade;

@Repository
public interface UserTestGradeRepository extends JpaRepository<UserTestGrade, UUID> {
    List<UserTestGrade> findByUser(User user);
    Optional<UserTestGrade> findByCourseAndUser(Course course, User user);
    List<UserTestGrade> findByCourse(Course course);
}