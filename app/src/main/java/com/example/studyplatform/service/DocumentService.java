package com.example.studyplatform.service;

import com.example.studyplatform.exception.FileStorageException;
import com.example.studyplatform.model.Document;
import com.example.studyplatform.repository.DocumentRepository;
import com.example.studyplatform.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MinioService minioService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, MinioService minioService) {
        this.documentRepository = documentRepository;
        this.minioService = minioService;
    }

    public Document uploadDocument(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();

        try {
            // Upload file to MinIO
            minioService.uploadFile(fileName, file.getInputStream(), fileType);

            // Save document information to the database
            Document document = new Document();
            document.setFileName(fileName);
            document.setFileType(fileType);
            document.setStorageLocation("minio/" + fileName); // Adjust as necessary
            return documentRepository.save(document);
        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
    }

    public Optional<Document> getDocument(Long documentId) {
        return documentRepository.findById(documentId);
    }
}