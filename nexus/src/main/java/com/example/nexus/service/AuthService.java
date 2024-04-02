package com.example.nexus.service;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.payload.request.AuthenticationRequest;
import com.example.nexus.model.payload.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    String login(AuthenticationRequest request);
    ResponseEntity<Profile> registerUser(RegisterRequest registerRequest);
}