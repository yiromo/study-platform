package com.example.studyplatform.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.studyplatform.exception.FileStorageException;
import com.example.studyplatform.model.Document;
import com.example.studyplatform.repository.DocumentRepository;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MinioService minioService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, MinioService minioService) {
        this.documentRepository = documentRepository;
        this.minioService = minioService;
    }

    public Document storeFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();

        try {
            minioService.uploadFile(file);

            Document document = new Document();
            document.setFileName(fileName);
            document.setFileType(fileType);
            document.setStorageLocation("minio/" + fileName); 
            return documentRepository.save(document);
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
            .orElse(null);
    }
}