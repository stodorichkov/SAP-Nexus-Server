package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.constant.RegexConstants;
import com.example.nexus.constant.RoleConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.exception.UnauthorizedException;
import com.example.nexus.exception.UserAlreadyExistsException;
import com.example.nexus.mapper.RegisterMapper;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.AuthenticationRequest;
import com.example.nexus.model.payload.request.RegisterRequest;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.RoleRepository;
import com.example.nexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final RegisterMapper registerMapper;

    @Override
    public String login(AuthenticationRequest request) {
        final var user = this.userRepository
                .findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException(MessageConstants.INVALID_USERNAME_PASSWORD));
        validateUserPassword(user, request);

        final var authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "token";
    }

    @Override
    public Profile registerUser(RegisterRequest registerRequest) {

        validateRequestedPassword(registerRequest);

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

    private void validateUserPassword(User user, AuthenticationRequest request) {
        final var encodedRealPassword = user.getPassword();
        if (!this.passwordEncoder.matches(request.password(), encodedRealPassword)) {
            throw new UnauthorizedException(MessageConstants.INVALID_USERNAME_PASSWORD);
        }
    }

    private void validateRequestedPassword(RegisterRequest registerRequest) {
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