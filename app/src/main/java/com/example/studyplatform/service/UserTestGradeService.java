package com.example.studyplatform.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studyplatform.dto.UserTestGradeDto;
import com.example.studyplatform.dto.UserTestGradeResponseDto;
import com.example.studyplatform.exception.ResourceNotFoundException;
import com.example.studyplatform.model.Course;
import com.example.studyplatform.model.User;
import com.example.studyplatform.model.UserTestGrade;
import com.example.studyplatform.repository.UserTestGradeRepository;

@Service
public class UserTestGradeService {

    private final UserTestGradeRepository userTestGradeRepository;
    private final CourseService courseService;
    
    @Autowired
    public UserTestGradeService(UserTestGradeRepository userTestGradeRepository, CourseService courseService) {
        this.userTestGradeRepository = userTestGradeRepository;
        this.courseService = courseService;
    }
    
    public List<UserTestGradeResponseDto> getAllGradesByUser(User user) {
        List<UserTestGrade> grades = userTestGradeRepository.findByUser(user);
        return grades.stream()
                .map(UserTestGradeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public UserTestGradeResponseDto getGradeByCourseAndUser(UUID courseId, User user) {
        Course course = courseService.getCourseById(courseId);
        UserTestGrade grade = userTestGradeRepository.findByCourseAndUser(course, user)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found for this course and user"));
        return UserTestGradeResponseDto.fromEntity(grade);
    }
    
    @Transactional
    public UserTestGradeResponseDto saveGrade(UUID courseId, UserTestGradeDto gradeDto, User user) {
        Course course = courseService.getCourseById(courseId);
        
        UserTestGrade grade = userTestGradeRepository.findByCourseAndUser(course, user)
                .orElse(new UserTestGrade());
        
        grade.setCourse(course);
        grade.setUser(user);
        grade.setCourseMaxTest(gradeDto.getCourseMaxTest());
        grade.setUserGrade(gradeDto.getUserGrade());
        
        grade = userTestGradeRepository.save(grade);
        return UserTestGradeResponseDto.fromEntity(grade);
    }
    
    @Transactional
    public UserTestGradeResponseDto updateGrade(UUID courseId, UserTestGradeDto gradeDto, User user) {
        Course course = courseService.getCourseById(courseId);
        
        UserTestGrade grade = userTestGradeRepository.findByCourseAndUser(course, user)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found for this course and user"));
        
        grade.setCourseMaxTest(gradeDto.getCourseMaxTest());
        grade.setUserGrade(gradeDto.getUserGrade());
        
        grade = userTestGradeRepository.save(grade);
        return UserTestGradeResponseDto.fromEntity(grade);
    }
    
    @Transactional
    public void deleteGrade(UUID id) {
        if (!userTestGradeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Grade not found with id: " + id);
        }
        userTestGradeRepository.deleteById(id);
    }
}