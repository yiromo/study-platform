package com.example.studyplatform.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RestController;

import com.example.studyplatform.dto.ModuleDto;
import com.example.studyplatform.exception.ResourceNotFoundException;
import com.example.studyplatform.model.CourseModule;
import com.example.studyplatform.model.User;
import com.example.studyplatform.service.ModuleService;
import com.example.studyplatform.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/modules")
@Tag(name = "Modules", description = "Course module management API")
public class ModuleController {
    private static final Logger logger = LoggerFactory.getLogger(ModuleController.class);
    
    private final ModuleService moduleService;
    private final UserService userService;
    
    @Autowired
    public ModuleController(ModuleService moduleService, UserService userService) {
        this.moduleService = moduleService;
        this.userService = userService;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping("/course/{courseId}")
    @Operation(
        summary = "Get all modules for a course", 
        description = "Retrieves a list of all modules for a course in ascending order by module number"
    )
    public ResponseEntity<Map<String, Object>> getAllModules(@PathVariable UUID courseId) {
        try {
            List<CourseModule> modules = moduleService.getModulesByCourse(courseId);
            Map<String, Object> response = new HashMap<>();
            response.put("total", modules.size());
            response.put("items", modules);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error retrieving modules for course {}: {}", courseId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/course/{courseId}")
    @Operation(
        summary = "Create a new module for a course", 
        description = "Creates a new module for a course (only the course creator can add modules)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CourseModule> createModule(
            @PathVariable UUID courseId,
            @RequestBody ModuleDto moduleDto) {
        try {
            CourseModule module = moduleService.createModule(courseId, moduleDto, getCurrentUser());
            return new ResponseEntity<>(module, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{moduleId}")
    @Operation(
        summary = "Update a module", 
        description = "Updates an existing module (only the course creator can update modules)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CourseModule> updateModule(
            @PathVariable UUID moduleId,
            @RequestBody ModuleDto moduleDto) {
        try {
            CourseModule module = moduleService.updateModule(moduleId, moduleDto, getCurrentUser());
            return ResponseEntity.ok(module);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{moduleId}")
    @Operation(
        summary = "Delete a module", 
        description = "Deletes a module and reorders the remaining modules (only the course creator can delete modules)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deleteModule(@PathVariable UUID moduleId) {
        moduleService.deleteModule(moduleId, getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}