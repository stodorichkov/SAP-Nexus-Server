package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.constant.RegexConstants;
import com.example.nexus.constant.RoleConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.exception.UnauthorizedException;
import com.example.nexus.exception.UserAlreadyExistsException;
import com.example.nexus.mapper.RegisterMapper;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.payload.request.RegisterRequest;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.RoleRepository;
import com.example.nexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final RegisterMapper registerMapper;

    @Override
    public Profile registerUser(RegisterRequest registerRequest) {

        validatePassword(registerRequest);

        var newUser = registerMapper.mapUser(registerRequest);
        isUserNameTaken(newUser.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.password()));
        roleRepository.findByName(RoleConstants.USER).
                ifPresentOrElse(userRole -> newUser.getRoles().add(userRole),
                        () -> {throw new NotFoundException(MessageConstants.ROLE_NOT_FOUNT);});

        var newProfile = registerMapper.mapProfile(registerRequest);
        newProfile.setUser(newUser);

        return profileRepository.save(newProfile);
    }

    private void validatePassword(RegisterRequest registerRequest) {
        if (!registerRequest.password().matches(RegexConstants.PASSWORD_REQUIREMENT_REGEX)) {
            throw new UnauthorizedException(MessageConstants.WRONG_PASSWORD_FORMAT);
        }
        if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
            throw new UnauthorizedException(MessageConstants.CONFIRM_PASSWORD_NOT_MATCHING);
        }
    }

    private void isUserNameTaken(String username) {
        userRepository.findByUsername(username).ifPresent(
                user -> {throw new UserAlreadyExistsException(MessageConstants.USER_ALREADY_EXISTS);}
        );
    }
}
