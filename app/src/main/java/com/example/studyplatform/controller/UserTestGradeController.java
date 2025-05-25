package com.example.studyplatform.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studyplatform.dto.UserTestGradeDto;
import com.example.studyplatform.dto.UserTestGradeResponseDto;
import com.example.studyplatform.model.User;
import com.example.studyplatform.service.UserService;
import com.example.studyplatform.service.UserTestGradeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user-grade")
@Tag(name = "User Test Grades", description = "User test grades management API")
public class UserTestGradeController {

    private final UserTestGradeService userTestGradeService;
    private final UserService userService;
    
    @Autowired
    public UserTestGradeController(UserTestGradeService userTestGradeService, UserService userService) {
        this.userTestGradeService = userTestGradeService;
        this.userService = userService;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping("/results")
    @Operation(
        summary = "Get all user grades", 
        description = "Get all test grades for the current user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Map<String, Object>> getAllUserGrades() {
        List<UserTestGradeResponseDto> grades = userTestGradeService.getAllGradesByUser(getCurrentUser());
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", grades.size());
        response.put("items", grades);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/results/{courseId}")
    @Operation(
        summary = "Get user grade for a course", 
        description = "Get the current user's test grade for a specific course",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserTestGradeResponseDto> getUserGradeByCourse(@PathVariable UUID courseId) {
        UserTestGradeResponseDto grade = userTestGradeService.getGradeByCourseAndUser(courseId, getCurrentUser());
        return ResponseEntity.ok(grade);
    }
    
    @PostMapping("/grade/{courseId}")
    @Operation(
        summary = "Save user grade for a course", 
        description = "Save the current user's test grade for a specific course",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserTestGradeResponseDto> saveUserGrade(
            @PathVariable UUID courseId,
            @RequestBody UserTestGradeDto gradeDto) {
        UserTestGradeResponseDto savedGrade = userTestGradeService.saveGrade(courseId, gradeDto, getCurrentUser());
        return new ResponseEntity<>(savedGrade, HttpStatus.CREATED);
    }
    
    @PutMapping("/grade/{courseId}")
    @Operation(
        summary = "Update user grade for a course", 
        description = "Update the current user's test grade for a specific course",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserTestGradeResponseDto> updateUserGrade(
            @PathVariable UUID courseId,
            @RequestBody UserTestGradeDto gradeDto) {
        UserTestGradeResponseDto updatedGrade = userTestGradeService.updateGrade(courseId, gradeDto, getCurrentUser());
        return ResponseEntity.ok(updatedGrade);
    }
}