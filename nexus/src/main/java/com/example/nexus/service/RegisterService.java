package com.example.nexus.service;

import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.RegisterRequest;

public interface RegisterService {
    String registerUser(RegisterRequest registerRequest);
}
