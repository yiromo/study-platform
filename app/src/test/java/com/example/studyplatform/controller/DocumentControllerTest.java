package com.example.studyplatform.controller;

import com.example.studyplatform.model.Document;
import com.example.studyplatform.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private DocumentService documentService;

    @BeforeEach
    public void setUp() {
        documentService = Mockito.mock(DocumentService.class);
    }

    @Test
    public void testUploadDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.docx", MediaType.APPLICATION_OCTET_STREAM_VALUE, "test content".getBytes());

        when(documentService.uploadDocument(any(MockMultipartFile.class))).thenReturn(new Document("test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "minio/path/test.docx"));

        mockMvc.perform(multipart("/api/documents/upload")
                .file(file))
                .andExpect(status().isOk());
    }
}