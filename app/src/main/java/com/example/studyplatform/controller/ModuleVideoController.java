package com.example.studyplatform.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.example.studyplatform.model.ModuleVideo;
import com.example.studyplatform.service.ModuleVideoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/modules/video")
@Tag(name = "Module Videos", description = "Module video management API")
public class ModuleVideoController {
    
    private final ModuleVideoService videoService;
    
    @Autowired
    public ModuleVideoController(ModuleVideoService videoService) {
        this.videoService = videoService;
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get video by ID", 
        description = "Retrieves video metadata by ID",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ModuleVideo> getVideo(@PathVariable UUID id) {
        try {
            ModuleVideo video = videoService.getVideoById(id);
            return ResponseEntity.ok(video);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload a video", 
        description = "Uploads a video file (up to 800MB) and returns video metadata",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ModuleVideo> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            ModuleVideo video = videoService.uploadVideo(file);
            return new ResponseEntity<>(video, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a video", 
        description = "Deletes a video by its ID",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deleteVideo(@PathVariable UUID id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "File size exceeds maximum limit of 800MB");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }
}