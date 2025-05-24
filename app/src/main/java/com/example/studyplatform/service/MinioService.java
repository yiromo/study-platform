package com.example.studyplatform.service;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName = "your-bucket-name"; // Replace with your bucket name

    @Autowired
    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void uploadFile(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(bucketName, file.getOriginalFilename(), inputStream, file.getSize(), null, null, null);
        } catch (MinioException e) {
            throw new Exception("Error occurred while uploading file: " + e.getMessage(), e);
        }
    }

    public InputStream downloadFile(String fileName) throws Exception {
        try {
            return minioClient.getObject(bucketName, fileName);
        } catch (MinioException e) {
            throw new Exception("Error occurred while downloading file: " + e.getMessage(), e);
        }
    }
}