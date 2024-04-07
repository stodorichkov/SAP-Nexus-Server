package com.example.nexus.service;

import com.example.nexus.exception.UnauthorizedException;
import com.example.nexus.model.entity.Role;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.AuthenticationRequest;
import com.example.nexus.model.payload.request.RegisterRequest;
import com.example.nexus.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private static String passwordHash;
    private static RegisterRequest registerRequest;
    private static Profile profile;
    private static Role role;

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
    @Mock
    private RegisterMapper registerMapper;
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
    
    @Test
    void registerUser_everythingIsCorrect_shouldSaveNewProfile() {
        when(registerMapper.mapProfile(registerRequest)).thenReturn(profile);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn(passwordHash);
        when(roleRepository.findByName(RoleConstants.USER)).
        thenReturn(Optional.of(role));

        this.authService.registerUser(registerRequest);

        final var profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(this.profileRepository).save(profileCaptor.capture());
        final var newProfile = profileCaptor.getValue();

        assertAll(
                () -> assertEquals("petar_g", newProfile.getUser().getUsername()),
                () -> assertEquals(passwordHash, newProfile.getUser().getPassword()),
                () -> assertEquals(RoleConstants.USER, newProfile.getUser().getRoles().get(0).getName()),
                () -> assertEquals("Petar", newProfile.getFirstName()),
                () -> assertEquals("Georgiev", newProfile.getLastName()),
                () -> assertEquals(0.0f, newProfile.getBalance())
        );
    }
}