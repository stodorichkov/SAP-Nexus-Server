package com.example.nexus.service;

import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.payload.request.RegisterRequest;

public interface RegisterService {
    Profile registerUser(RegisterRequest registerRequest);
}
