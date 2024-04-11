package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.payload.request.AddMoneyRequest;
import com.example.nexus.repository.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final JwtService jwtService;
    private final ProfileRepository profileRepository;

    @SneakyThrows
    @Override
    public void addMoney(AddMoneyRequest addMoneyRequest, HttpServletRequest request) {
        final var token = this.jwtService.getTokenFromRequest(request);
        final var username = jwtService.getUsernameFromToken(token);


        Profile profile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException(MessageConstants.USER_NOT_FOUND));

        float requestedMoney = addMoneyRequest.money();
        float currentBalance = profile.getBalance();
        float updatedBalance = currentBalance + requestedMoney;
        profile.setBalance(updatedBalance);

        profileRepository.save(profile);
    }
}