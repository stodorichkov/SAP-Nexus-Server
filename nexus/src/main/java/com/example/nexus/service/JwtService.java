package com.example.nexus.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtService {
    String generateToken(Authentication authentication);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
    String getTokenFromRequest(HttpServletRequest request);
}