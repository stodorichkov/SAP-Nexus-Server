package com.example.nexus.service;

import com.example.nexus.exception.UnauthorizedException;
import com.example.nexus.model.entity.Role;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.AuthenticationRequest;
import com.example.nexus.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTests {
    private static AuthenticationRequest request;
    private static User user;
    private static String token;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeAll
    static void setUp() {
        final var username = "username";
        final var passwordHash = "hashedPassword";
        final var role = new Role();

        request = new AuthenticationRequest(username, passwordHash);

        role.setId(1L);
        role.setName("USER");

        user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(passwordHash);
        user.getRoles().add(role);

        token = "token";
    }

    @Test
    void login_userNotExist_expectUnauthorizedException() {
        assertThrows(UnauthorizedException.class, () -> this.authService.login(request));
    }

    @Test
    void login_invalidPassword_expectUnauthorizedException() {
        when(this.userRepository.findByUsername(request.username())).thenReturn(Optional.of(new User()));

        assertThrows(UnauthorizedException.class, () -> this.authService.login(request));
    }

    @Test
    void login_userExist_expectToken() {
        when(this.userRepository.findByUsername(request.username())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn(token);

        final var result = this.authService.login(request);

        verify(authenticationManager)
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        assertEquals(token, result);
    }
}