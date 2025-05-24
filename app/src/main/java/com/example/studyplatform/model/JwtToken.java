package com.example.studyplatform.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokens")
public class JwtToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private boolean isRefreshToken;
    
    @Column(nullable = false)
    private Instant expiryDate;
    
    @Column(nullable = false)
    private boolean revoked;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructors, getters, and setters
    public JwtToken() {
    }
    
    public JwtToken(String token, boolean isRefreshToken, Instant expiryDate, User user) {
        this.token = token;
        this.isRefreshToken = isRefreshToken;
        this.expiryDate = expiryDate;
        this.user = user;
        this.revoked = false;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public boolean isRefreshToken() {
        return isRefreshToken;
    }
    
    public void setRefreshToken(boolean refreshToken) {
        isRefreshToken = refreshToken;
    }
    
    public Instant getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public boolean isRevoked() {
        return revoked;
    }
    
    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}