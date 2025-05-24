package com.example.studyplatform.service;

import com.example.studyplatform.config.JwtConfig;
import com.example.studyplatform.model.JwtToken;
import com.example.studyplatform.model.User;
import com.example.studyplatform.repository.JwtTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtTokenService {

    private final JwtTokenRepository tokenRepository;
    private final JwtConfig jwtConfig;
    private final Key key;

    @Autowired
    public JwtTokenService(JwtTokenRepository tokenRepository, JwtConfig jwtConfig) {
        this.tokenRepository = tokenRepository;
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), jwtConfig.getAccessTokenExpirationMs());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), jwtConfig.getRefreshTokenExpirationMs());
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationMs) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtToken saveToken(String token, User user, boolean isRefreshToken, long expirationMs) {
        JwtToken jwtToken = new JwtToken(
                token,
                isRefreshToken,
                Instant.now().plusMillis(expirationMs),
                user
        );
        return tokenRepository.save(jwtToken);
    }

    public Optional<JwtToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            
            // Also check if token is in database and not revoked
            Optional<JwtToken> storedToken = tokenRepository.findByToken(token);
            return storedToken.isPresent() && !storedToken.get().isRevoked() && !storedToken.get().isExpired();
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public void revokeAllUserTokens(User user) {
        List<JwtToken> validTokens = tokenRepository.findAllByUserAndRevoked(user, false);
        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(token -> {
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validTokens);
    }
}