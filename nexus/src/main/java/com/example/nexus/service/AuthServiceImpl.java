package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.UnauthorizedException;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.AuthenticationRequest;
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

    private void validateUserPassword(User user, AuthenticationRequest request) {
        final var encodedRealPassword = user.getPassword();

        if(!this.passwordEncoder.matches(request.password(), encodedRealPassword)) {
            throw new UnauthorizedException(MessageConstants.INVALID_USERNAME_PASSWORD);
        }
    }
}