package com.example.studyplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.studyplatform.config.JwtConfig;
import com.example.studyplatform.dto.AuthResponse;
import com.example.studyplatform.dto.LoginRequest;
import com.example.studyplatform.dto.RegisterRequest;
import com.example.studyplatform.model.User;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public AuthenticationService(
            UserService userService,
            JwtTokenService jwtTokenService,
            AuthenticationManager authenticationManager,
            JwtConfig jwtConfig,
            CustomUserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setUserType(request.getUserType());
        
        User savedUser = userService.registerUser(user);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        
        String accessToken = jwtTokenService.generateAccessToken(userDetails);
        String refreshToken = jwtTokenService.generateRefreshToken(userDetails);
        
        // Save tokens to database
        jwtTokenService.saveToken(accessToken, savedUser, false, jwtConfig.getAccessTokenExpirationMs());
        jwtTokenService.saveToken(refreshToken, savedUser, true, jwtConfig.getRefreshTokenExpirationMs());
        
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Revoke all existing tokens for this user
        jwtTokenService.revokeAllUserTokens(user);
        
        // Generate new tokens
        String accessToken = jwtTokenService.generateAccessToken(userDetails);
        String refreshToken = jwtTokenService.generateRefreshToken(userDetails);
        
        // Save tokens to database
        jwtTokenService.saveToken(accessToken, user, false, jwtConfig.getAccessTokenExpirationMs());
        jwtTokenService.saveToken(refreshToken, user, true, jwtConfig.getRefreshTokenExpirationMs());
        
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtTokenService.extractUsername(refreshToken);
        
        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (jwtTokenService.validateToken(refreshToken)) {
                String accessToken = jwtTokenService.generateAccessToken(userDetails);
                
                jwtTokenService.findByToken(refreshToken)
                        .orElseThrow(() -> new RuntimeException("Refresh token not found"));
                
                jwtTokenService.saveToken(accessToken, user, false, jwtConfig.getAccessTokenExpirationMs());
                
                return new AuthResponse(accessToken, refreshToken);
            }
        }
        
        throw new RuntimeException("Invalid refresh token");
    }
}