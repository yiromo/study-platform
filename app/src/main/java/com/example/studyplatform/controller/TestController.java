package com.example.studyplatform.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.example.studyplatform.dto.TestDto;
import com.example.studyplatform.model.Test;
import com.example.studyplatform.service.TestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/modules/tests")
@Tag(name = "Tests", description = "Test management API")
@PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
public class TestController {

    private final TestService testService;
    
    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }
    
    @GetMapping
    @Operation(
        summary = "Get all tests", 
        description = "Retrieves all tests (only for professors)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<Test>> getAllTests() {
        return ResponseEntity.ok(testService.getAllTests());
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get test by ID", 
        description = "Retrieves a test by its ID (only for professors)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Test> getTestById(@PathVariable UUID id) {
        return ResponseEntity.ok(testService.getTestById(id));
    }
    
    @PostMapping
    @Operation(
        summary = "Create a new test", 
        description = "Creates a new test (only for professors)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Test> createTest(@RequestBody TestDto testDto) {
        Test test = testService.createTest(testDto);
        return new ResponseEntity<>(test, HttpStatus.CREATED);
    }
    
    @PostMapping(value = "/create-from-docx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Create a test from DOCX file", 
        description = "Creates a new test by parsing a DOCX file with a specific structure (only for professors)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> createTestFromDocx(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".docx")) {
                return ResponseEntity.badRequest().body("Only DOCX files are supported");
            }
            
            Test test = testService.createTestFromDocx(file);
            return new ResponseEntity<>(test, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to process DOCX file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a test", 
        description = "Updates an existing test (only for professors)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Test> updateTest(@PathVariable UUID id, @RequestBody TestDto testDto) {
        Test test = testService.updateTest(id, testDto);
        return ResponseEntity.ok(test);
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a test", 
        description = "Deletes a test (only for professors)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deleteTest(@PathVariable UUID id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }
}