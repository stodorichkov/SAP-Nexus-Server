package com.example.nexus.service;

import com.example.nexus.exception.UnauthorizedException;
import com.example.nexus.mapper.RegisterMapper;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.Role;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.RegisterRequest;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.RoleRepository;
import com.example.nexus.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final RegisterMapper registerMapper;

    @Override
    @Transactional
    public Profile registerUser(RegisterRequest registerRequest) {

        if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
            throw new UnauthorizedException("Repeated password doesn't match original.");
        }

        var newUser = registerMapper.mapUser(registerRequest);
        newUser.setPassword(passwordEncoder.encode(registerRequest.password()));
        roleRepository.findByName("User").
                ifPresent(userRole -> newUser.getRoles().add(userRole));
        userRepository.save(newUser);

        var newProfile = registerMapper.mapProfile(registerRequest);
        newProfile.setUser(newUser);

        return profileRepository.save(newProfile);
    }
}
