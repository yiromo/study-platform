package com.example.studyplatform.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.studyplatform.dto.CourseDto;
import com.example.studyplatform.exception.ResourceNotFoundException;
import com.example.studyplatform.model.Course;
import com.example.studyplatform.model.User;
import com.example.studyplatform.model.UserType;
import com.example.studyplatform.repository.CourseRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final MinioService minioService;
    
    @Autowired
    public CourseService(CourseRepository courseRepository, MinioService minioService) {
        this.courseRepository = courseRepository;
        this.minioService = minioService;
    }
    
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    public Course getCourseById(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }
    
    public Course createCourse(CourseDto courseDto, User currentUser) {
        if (currentUser.getUserType() != UserType.PROFESSOR) {
            throw new AccessDeniedException("Only professors can create courses");
        }
        
        Course course = new Course();
        course.setUploadedUser(currentUser);
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setApproximateTime(courseDto.getApproximateTime());
        
        return courseRepository.save(course);
    }
    
    public Course updateCourse(UUID id, CourseDto courseDto, User currentUser) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        if (!course.getUploadedUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this course");
        }
        
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setApproximateTime(courseDto.getApproximateTime());
        
        return courseRepository.save(course);
    }
    
    public void deleteCourse(UUID id, User currentUser) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        if (!course.getUploadedUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this course");
        }
        
        if (course.getImageUrl() != null && !course.getImageUrl().isEmpty()) {
            try {
                String imageFileName = course.getImageUrl().substring(course.getImageUrl().lastIndexOf("/") + 1);
                minioService.deleteFile(imageFileName);
            } catch (Exception e) {
                System.err.println("Error deleting course image: " + e.getMessage());
            }
        }
        
        courseRepository.delete(course);
    }
    
    public Course uploadCourseImage(UUID courseId, MultipartFile file, User currentUser) throws Exception {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        if (!course.getUploadedUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this course image");
        }
        
        String fileName = "http://194.110.54.189:9150/objects/" + file.getOriginalFilename();
        
        minioService.uploadFile(file);
        
        course.setImageUrl(fileName);
        return courseRepository.save(course);
    }
    
    public void deleteCourseImage(UUID courseId, User currentUser) throws Exception {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        if (!course.getUploadedUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this course image");
        }
        
        if (course.getImageUrl() == null || course.getImageUrl().isEmpty()) {
            throw new ResourceNotFoundException("Course does not have an image");
        }
        
        String imageFileName = course.getImageUrl().substring(course.getImageUrl().lastIndexOf("/") + 1);
        
        minioService.deleteFile(imageFileName);
        
        course.setImageUrl(null);
        courseRepository.save(course);
    }
}