package com.example.nexus.service;

import com.example.nexus.model.payload.request.AuthenticationRequest;

public interface AuthService {
    String login(AuthenticationRequest request);
}
