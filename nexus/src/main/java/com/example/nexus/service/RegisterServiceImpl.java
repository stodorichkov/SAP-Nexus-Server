package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.constant.RoleConstants;
import com.example.nexus.exception.NotFoundException;
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
            throw new UnauthorizedException(MessageConstants.CONFIRM_PASSWORD_NOT_MATCHING);
        }

        var newUser = registerMapper.mapUser(registerRequest);
        newUser.setPassword(passwordEncoder.encode(registerRequest.password()));
        roleRepository.findByName(RoleConstants.USER).
                ifPresentOrElse(userRole -> newUser.getRoles().add(userRole),
                        () -> new NotFoundException(MessageConstants.ROLE_NOT_FOUNT));
        userRepository.save(newUser);

        var newProfile = registerMapper.mapProfile(registerRequest);
        newProfile.setUser(newUser);

        return profileRepository.save(newProfile);
    }
}
