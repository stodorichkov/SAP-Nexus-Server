package com.example.nexus.service;

import com.example.nexus.model.payload.response.UserResponse;
import org.springframework.data.domain.Page;


public interface AdminService {
    Page<UserResponse> getUsers(int pageNumber);
}
