package com.example.studyplatform.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studyplatform.dto.ModuleDto;
import com.example.studyplatform.exception.AccessDeniedException;
import com.example.studyplatform.exception.ResourceNotFoundException;
import com.example.studyplatform.model.Course;
import com.example.studyplatform.model.CourseModule;
import com.example.studyplatform.model.ModuleType;
import com.example.studyplatform.model.ModuleVideo;
import com.example.studyplatform.model.Test;
import com.example.studyplatform.model.User;
import com.example.studyplatform.repository.CourseModuleRepository;

@Service
public class ModuleService {

    private final CourseModuleRepository moduleRepository;
    private final CourseService courseService;
    private final TestService testService;
    private final ModuleVideoService videoService;
    
    @Autowired
    public ModuleService(CourseModuleRepository moduleRepository, CourseService courseService, 
                         TestService testService, ModuleVideoService videoService) {
        this.moduleRepository = moduleRepository;
        this.courseService = courseService;
        this.testService = testService;
        this.videoService = videoService;
    }
    
    public List<CourseModule> getModulesByCourse(UUID courseId) {
        courseService.getCourseById(courseId); 
        return moduleRepository.findByCourseIdOrderByModuleNumAsc(courseId);
    }
    
    @Transactional
    public CourseModule createModule(UUID courseId, ModuleDto moduleDto, User currentUser) throws Exception {
        Course course = courseService.getCourseById(courseId);
        
        if (!course.getUploadedUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to add modules to this course");
        }
        
        int moduleNum = 1; 
        Optional<Integer> maxModuleNum = moduleRepository.findMaxModuleNumByCourseId(courseId);
        if (maxModuleNum.isPresent()) {
            moduleNum = maxModuleNum.get() + 1;
        }
        
        CourseModule module = new CourseModule(course, moduleDto.getModuleType(), moduleNum, moduleDto.getModuleTitle());
        
        switch (moduleDto.getModuleType()) {
            case TEXT -> {
                if (moduleDto.getText() == null || moduleDto.getText().isEmpty()) {
                    throw new IllegalArgumentException("Text content is required for TEXT module type");
                }
                module.setText(moduleDto.getText());
            }
            case VIDEO -> {
                if (moduleDto.getVideoId() == null || moduleDto.getVideoId().isEmpty()) {
                    throw new IllegalArgumentException("Video ID is required for VIDEO module type");
                }
                
                ModuleVideo video = videoService.getVideoById(UUID.fromString(moduleDto.getVideoId()));
                module.setVideo(video);
            }
            case TEST -> {
                if (moduleDto.getTestId() == null || moduleDto.getTestId().isEmpty()) {
                    throw new IllegalArgumentException("Test ID is required for TEST module type");
                }
                
                Test test = testService.getTestById(UUID.fromString(moduleDto.getTestId()));
                module.setTest(test);
            }
        }
        
        return moduleRepository.save(module);
    }
    
    @Transactional
    public CourseModule updateModule(UUID moduleId, ModuleDto moduleDto, User currentUser) throws Exception {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));
        
        Course course = module.getCourse();
        
        if (!course.getUploadedUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update modules in this course");
        }
        
        module.setModuleTitle(moduleDto.getModuleTitle());
        
        if (module.getModuleType() != moduleDto.getModuleType()) {
            module.setText(null);
            module.setVideo(null);
            module.setTest(null);
            
            module.setModuleType(moduleDto.getModuleType());
        }
        
        switch (moduleDto.getModuleType()) {
            case TEXT -> {
                if (moduleDto.getText() == null || moduleDto.getText().isEmpty()) {
                    throw new IllegalArgumentException("Text content is required for TEXT module type");
                }
                module.setText(moduleDto.getText());
            }
            case VIDEO -> {
                if (moduleDto.getVideoId() == null || moduleDto.getVideoId().isEmpty()) {
                    throw new IllegalArgumentException("Video ID is required for VIDEO module type");
                }
                
                ModuleVideo video = videoService.getVideoById(UUID.fromString(moduleDto.getVideoId()));
                module.setVideo(video);
            }
            case TEST -> {
                if (moduleDto.getTestId() == null || moduleDto.getTestId().isEmpty()) {
                    throw new IllegalArgumentException("Test ID is required for TEST module type");
                }
                
                Test test = testService.getTestById(UUID.fromString(moduleDto.getTestId()));
                module.setTest(test);
            }
        }
        
        return moduleRepository.save(module);
    }
    
    @Transactional
    public void deleteModule(UUID moduleId, User currentUser) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));
        
        Course course = module.getCourse();
        
        if (!course.getUploadedUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to delete modules from this course");
        }
        
        int deletedModuleNum = module.getModuleNum();
        UUID courseId = course.getId();
        
        ModuleVideo video = null;
        if (module.getModuleType() == ModuleType.VIDEO) {
            video = module.getVideo();
        }
        
        moduleRepository.delete(module);
        
        List<CourseModule> subsequentModules = moduleRepository.findByCourseIdAndModuleNumGreaterThanOrderByModuleNumAsc(
                courseId, deletedModuleNum);
        
        subsequentModules.forEach(m -> {
            m.setModuleNum(m.getModuleNum() - 1);
            moduleRepository.save(m);
        });
        
        if (video != null) {
            try {
                boolean videoInUse = moduleRepository.existsByVideoId(video.getId());
                if (!videoInUse) {
                    videoService.deleteVideo(video.getId());
                }
            } catch (Exception e) {
                System.err.println("Failed to delete video: " + e.getMessage());
            }
        }
    }
}