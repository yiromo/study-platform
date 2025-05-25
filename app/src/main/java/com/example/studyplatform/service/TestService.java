package com.example.studyplatform.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studyplatform.dto.TestDto;
import com.example.studyplatform.exception.ResourceNotFoundException;
import com.example.studyplatform.model.Test;
import com.example.studyplatform.repository.TestRepository;

@Service
public class TestService {

    private final TestRepository testRepository;
    
    @Autowired
    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
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
}