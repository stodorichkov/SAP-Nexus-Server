package com.example.nexus.service;

import com.example.nexus.model.payload.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    void seedAdmin();
    Page<UserResponse> getUsers(Pageable pageable);
    void addUserRole(String username);
    void removeUserRole(String username);
}