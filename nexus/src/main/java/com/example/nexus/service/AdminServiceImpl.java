package com.example.nexus.service;

import com.example.nexus.constant.PageConstants;
import com.example.nexus.mapper.UserMapper;
import com.example.nexus.model.payload.response.UserResponse;
import com.example.nexus.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
    private final ProfileRepository profileRepository;
    private final UserMapper userMapper;
    @Override
    public Page<UserResponse> getUsers(int pageNumber) {
        final var pageable = PageRequest.of(pageNumber, PageConstants.USER_PAGE_SIZE);

        return this.profileRepository
                .findAll(pageable)
                .map(this.userMapper::map);
    }
}