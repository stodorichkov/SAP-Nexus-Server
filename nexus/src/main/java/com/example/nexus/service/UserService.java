package com.example.nexus.service;

import com.example.nexus.model.payload.response.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {
    void seedAdmin();
    Page<UserResponse> getUsers(int pageNumber);
}