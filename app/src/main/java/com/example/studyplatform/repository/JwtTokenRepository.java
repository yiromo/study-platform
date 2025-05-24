package com.example.studyplatform.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.studyplatform.model.JwtToken;
import com.example.studyplatform.model.User;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, UUID> {
    Optional<JwtToken> findByToken(String token);
    List<JwtToken> findAllByUserAndRevoked(User user, boolean revoked);
    
    @Query("SELECT t FROM JwtToken t WHERE t.user.id = :userId AND t.isRefreshToken = true AND t.revoked = false")
    List<JwtToken> findActiveRefreshTokensByUserId(UUID userId);
}