package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProfileMapper;
import com.example.nexus.model.payload.request.AddMoneyRequest;
import com.example.nexus.model.payload.response.ProfileResponse;
import com.example.nexus.repository.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final JwtService jwtService;
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public void addMoney(AddMoneyRequest addMoneyRequest, HttpServletRequest request) {
        final var token = this.jwtService.getTokenFromRequest(request);
        final var username = jwtService.getUsernameFromToken(token);

        final var profile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException(MessageConstants.USER_NOT_FOUND));

        final var requestedMoney = addMoneyRequest.money();
        final var currentBalance = profile.getBalance();
        final var updatedBalance = currentBalance + requestedMoney;

        profile.setBalance(updatedBalance);

        profileRepository.save(profile);
    }

    @Override
    public ProfileResponse getProfileInfo(HttpServletRequest request) {
        final var username = this.getUsernameFromAuthRequest(request);

        final var profile = this.profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException(MessageConstants.PROFILE_NOT_FOUND));

        return this.profileMapper.profileToProfileResponse(profile);
    }

    private String getUsernameFromAuthRequest(HttpServletRequest request) {
        final var token = this.jwtService.getTokenFromRequest(request);

        return this.jwtService.getUsernameFromToken(token);
    }
}