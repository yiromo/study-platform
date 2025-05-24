package com.example.studyplatform.service;

import com.example.studyplatform.exception.FileStorageException;
import com.example.studyplatform.model.Document;
import com.example.studyplatform.repository.DocumentRepository;
import com.example.studyplatform.service.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private MinioService minioService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadDocument() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.docx");
        when(file.getContentType()).thenReturn("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        when(file.getBytes()).thenReturn("test content".getBytes());

        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        
        when(minioService.uploadFile(any(MultipartFile.class))).thenReturn("minio-url/test.docx");
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        Document uploadedDocument = documentService.uploadDocument(file);

        verify(minioService, times(1)).uploadFile(file);
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    public void testUploadDocumentThrowsException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.docx");
        when(file.getContentType()).thenReturn("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        when(file.getBytes()).thenThrow(new IOException("File error"));

        try {
            documentService.uploadDocument(file);
        } catch (FileStorageException e) {
            // Expected exception
        }

        verify(minioService, never()).uploadFile(file);
        verify(documentRepository, never()).save(any(Document.class));
    }
}