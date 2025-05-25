package com.example.studyplatform.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.studyplatform.exception.ResourceNotFoundException;
import com.example.studyplatform.model.ModuleVideo;
import com.example.studyplatform.repository.ModuleVideoRepository;

@Service
public class ModuleVideoService {
    
    private final ModuleVideoRepository videoRepository;
    private final MinioService minioService;
    
    @Autowired
    public ModuleVideoService(ModuleVideoRepository videoRepository, MinioService minioService) {
        this.videoRepository = videoRepository;
        this.minioService = minioService;
    }
    
    @Transactional
    public ModuleVideo uploadVideo(MultipartFile videoFile) throws Exception {
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("Video file is required");
        }
        
        String fileName = "http://194.110.54.189:9150/objects/" + videoFile.getOriginalFilename();
        
        minioService.uploadFile(videoFile);
        ModuleVideo video = new ModuleVideo(fileName);
        
        return videoRepository.save(video);
    }
    
    @Transactional
    public void deleteVideo(UUID videoId) throws Exception {
        ModuleVideo video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + videoId));
        
        try {
            minioService.deleteFile(video.getVideoUrl());
        } catch (Exception e) {
            System.err.println("Failed to delete video file: " + e.getMessage());
        }
        
        videoRepository.delete(video);
    }
    
    public ModuleVideo getVideoById(UUID id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
    }
}