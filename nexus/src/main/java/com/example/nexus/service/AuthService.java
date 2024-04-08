package com.example.nexus.service;

import com.example.nexus.model.payload.request.AuthenticationRequest;
import com.example.nexus.model.payload.request.RegisterRequest;

public interface AuthService {
    String login(AuthenticationRequest request);
    void registerUser(RegisterRequest registerRequest);
}