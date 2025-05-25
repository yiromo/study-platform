package com.example.studyplatform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.studyplatform.dto.CourseDto;
import com.example.studyplatform.model.Course;
import com.example.studyplatform.model.User;
import com.example.studyplatform.service.CourseService;
import com.example.studyplatform.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Course management API")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    
    @Autowired
    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieves a list of all courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieves a course by its ID")
    public ResponseEntity<Course> getCourseById(@PathVariable UUID id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }
    
    @PostMapping
    @Operation(
        summary = "Create a new course", 
        description = "Creates a new course (only professors can create courses)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
    public ResponseEntity<Course> createCourse(@RequestBody CourseDto courseDto) {
        Course createdCourse = courseService.createCourse(courseDto, getCurrentUser());
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a course", 
        description = "Updates an existing course (only the course creator can update it)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Course> updateCourse(@PathVariable UUID id, @RequestBody CourseDto courseDto) {
        Course updatedCourse = courseService.updateCourse(id, courseDto, getCurrentUser());
        return ResponseEntity.ok(updatedCourse);
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a course", 
        description = "Deletes an existing course (only the course creator can delete it)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id, getCurrentUser());
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/image")
    @Operation(
        summary = "Upload course image", 
        description = "Uploads an image for a course (only the course creator can upload an image)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Course> uploadCourseImage(
            @PathVariable UUID id, 
            @RequestParam("image") MultipartFile image) {
        try {
            Course course = courseService.uploadCourseImage(id, image, getCurrentUser());
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}/image")
    @Operation(
        summary = "Delete course image", 
        description = "Deletes the image of a course (only the course creator can delete the image)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deleteCourseImage(@PathVariable UUID id) {
        try {
            courseService.deleteCourseImage(id, getCurrentUser());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}