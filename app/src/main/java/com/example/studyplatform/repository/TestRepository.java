package com.example.studyplatform.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studyplatform.model.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, UUID> {
}