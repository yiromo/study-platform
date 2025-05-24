package com.example.studyplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition
public class StudyPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyPlatformApplication.class, args);
    }
}