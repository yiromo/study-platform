package com.example.studyplatform.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.studyplatform.dto.TestDto;
import com.example.studyplatform.exception.ResourceNotFoundException;
import com.example.studyplatform.model.Test;
import com.example.studyplatform.repository.TestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TestService {

    private final TestRepository testRepository;
    private final ObjectMapper objectMapper;
    
    
    @Autowired
    public TestService(TestRepository testRepository, ObjectMapper objectMapper) {
        this.testRepository = testRepository;
        this.objectMapper = objectMapper;
    }
    
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }
    
    public Test getTestById(UUID id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + id));
    }
    
    public Test createTest(TestDto testDto) {
        Test test = new Test();
        test.setBody(testDto.getBody());
        return testRepository.save(test);
    }
    
    public Test updateTest(UUID id, TestDto testDto) {
        Test test = getTestById(id);
        test.setBody(testDto.getBody());
        return testRepository.save(test);
    }
    
    public void deleteTest(UUID id) {
        Test test = getTestById(id);
        testRepository.delete(test);
    }

    public Test createTestFromDocx(MultipartFile file) throws IOException {
        List<Map<String, Object>> questions = parseDocxToQuestions(file.getInputStream());
        String jsonBody = objectMapper.writeValueAsString(questions);
        
        Test test = new Test();
        test.setBody(jsonBody);
        return testRepository.save(test);
    }
    
    private List<Map<String, Object>> parseDocxToQuestions(InputStream inputStream) throws IOException {
        List<Map<String, Object>> questions = new ArrayList<>();
        int questionNum = 1;
        
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            Map<String, Object> currentQuestion = null;
            Map<String, String> variants = new HashMap<>();
            String correctAnswer = "var1"; // Default
            
            Pattern questionPattern = Pattern.compile("<question>\\s*(.+)");
            Pattern variantPattern = Pattern.compile("<variant>\\s*(.+)");
            
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText().trim();
                
                if (text.isEmpty()) {
                    continue;
                }
                
                Matcher questionMatcher = questionPattern.matcher(text);
                if (questionMatcher.find()) {
                    // If we already have a question in progress, save it before starting a new one
                    if (currentQuestion != null) {
                        currentQuestion.put("ans_variants", variants);
                        currentQuestion.put("correct_var", correctAnswer);
                        questions.add(currentQuestion);
                        questionNum++;
                    }
                    
                    // Initialize new question and variants map
                    currentQuestion = new HashMap<>();
                    variants = new HashMap<>();
                    currentQuestion.put("question_num", questionNum);
                    currentQuestion.put("question", questionMatcher.group(1).trim());
                    correctAnswer = "var1"; // First variant is always correct
                    continue;  // Process next line after starting a new question
                }
                
                Matcher variantMatcher = variantPattern.matcher(text);
                if (variantMatcher.find() && currentQuestion != null) {
                    int variantCount = variants.size() + 1;
                    String varKey = "var" + variantCount;
                    variants.put(varKey, variantMatcher.group(1).trim());
                }
            }
            
            // Don't forget to add the last question if it exists
            if (currentQuestion != null) {
                currentQuestion.put("ans_variants", variants);
                currentQuestion.put("correct_var", correctAnswer);
                questions.add(currentQuestion);
            }
        }
        
        return questions;
    }
}