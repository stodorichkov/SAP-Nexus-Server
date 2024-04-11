package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.constant.RoleConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.exception.UnauthorizedException;
import com.example.nexus.exception.UserAlreadyExistsException;
import com.example.nexus.mapper.RegisterMapper;
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
    private final JwtService jwtService;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final RegisterMapper registerMapper;

    @Override
    public String login(AuthenticationRequest authenticationRequest) {
        final var user = this.userRepository
                .findByUsername(authenticationRequest.username())
                .orElseThrow(() -> new UnauthorizedException(MessageConstants.INVALID_USERNAME_PASSWORD));
        validateUserPassword(user, authenticationRequest);

        final var authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.username(),
                        authenticationRequest.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return this.jwtService.generateToken(authentication);
    }

    @Override
    public void registerUser(RegisterRequest registerRequest) {
        if (this.userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new UserAlreadyExistsException(MessageConstants.USER_EXISTS);
        }

        final var newProfile = this.registerMapper.mapProfile(registerRequest);
        newProfile.getUser().setPassword(this.passwordEncoder.encode(registerRequest.password()));
        final var role = this.roleRepository
                .findByName(RoleConstants.USER)
                .orElseThrow(() -> new NotFoundException(MessageConstants.ROLE_NOT_FOUNT));
        newProfile.getUser().getRoles().add(role);

        profileRepository.save(newProfile);
    }

    private void validateUserPassword(User user, AuthenticationRequest request) {
        if(!this.passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException(MessageConstants.INVALID_USERNAME_PASSWORD);
        }
    }
}