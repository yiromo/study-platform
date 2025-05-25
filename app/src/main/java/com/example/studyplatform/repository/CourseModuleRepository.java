package com.example.studyplatform.repository;

import com.example.studyplatform.model.Course;
import com.example.studyplatform.model.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, UUID> {
    List<CourseModule> findByCourseIdOrderByModuleNumAsc(UUID courseId);
    
    Optional<CourseModule> findFirstByCourseOrderByModuleNumDesc(Course course);
    
    @Query("SELECT MAX(m.moduleNum) FROM CourseModule m WHERE m.course.id = :courseId")
    Optional<Integer> findMaxModuleNumByCourseId(@Param("courseId") UUID courseId);
    
    List<CourseModule> findByCourseIdAndModuleNumGreaterThanOrderByModuleNumAsc(UUID courseId, Integer moduleNum);
}