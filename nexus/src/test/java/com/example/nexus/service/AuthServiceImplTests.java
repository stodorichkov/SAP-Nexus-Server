package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.constant.RoleConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.exception.UnauthorizedException;
import com.example.nexus.exception.UserAlreadyExistsException;
import com.example.nexus.mapper.RegisterMapper;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.Role;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.RegisterRequest;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.RoleRepository;
import com.example.nexus.model.payload.request.AuthenticationRequest;
import com.example.nexus.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTests {
    private static String passwordHash;
    private static RegisterRequest registerRequest;
    private static Profile profile;
    private static Role role;
    private static AuthenticationRequest authRequest;
    private static User user;
    private static String token;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RegisterMapper registerMapper;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeAll
    static void setUp() {
        passwordHash = "hashedPassword";
        token = "token";

        registerRequest = new RegisterRequest("Petar", "Georgiev", "petar_g",
                "123456aA", "123456aA");
        authRequest = new AuthenticationRequest("petar_g", passwordHash);

        role = new Role();
        role.setName("USER");

        user = new User();
        user.setId(1L);
        user.setUsername("petar_g");
        user.setPassword(passwordHash);
        user.getRoles().add(role);

        profile = new Profile();
        profile.setId(1L);
        profile.setFirstName("Petar");
        profile.setLastName("Georgiev");
        profile.setBalance(0.0f);
        profile.setUser(user);
    }
    @Test
    void registerUser_userAlreadyExists_expectUserAlreadyExistsException() {
        when(userRepository.findByUsername("petar_g")).thenReturn(Optional.of(new User()));

        assertThatExceptionOfType(UserAlreadyExistsException.class).
                isThrownBy(() -> this.authService.registerUser(registerRequest)).
                withMessage(MessageConstants.USER_EXISTS);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_noRoleInDatabase_expectNotFoundException() {
        when(userRepository.findByUsername("petar_g")).thenReturn(Optional.empty());
        when(registerMapper.mapProfile(registerRequest)).thenReturn(profile);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn(passwordHash);
        when(roleRepository.findByName(RoleConstants.USER)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class).
                isThrownBy(() -> this.authService.registerUser(registerRequest)).
                withMessage(MessageConstants.ROLE_NOT_FOUNT);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_everythingIsCorrect_expectSaveNewProfile() {
        when(userRepository.findByUsername("petar_g")).thenReturn(Optional.empty());
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

    @Test
    void login_userNotExist_expectUnauthorizedException() {
        assertThrows(UnauthorizedException.class, () -> this.authService.login(authRequest));
    }

    @Test
    void login_invalidPassword_expectUnauthorizedException() {
        when(this.userRepository.findByUsername(authRequest.username())).thenReturn(Optional.of(new User()));

        assertThrows(UnauthorizedException.class, () -> this.authService.login(authRequest));
    }

    @Test
    void login_userExist_expectToken() {
        when(this.userRepository.findByUsername(authRequest.username())).thenReturn(Optional.of(user));
        when(this.passwordEncoder.matches(authRequest.password(), user.getPassword())).thenReturn(true);
        when(this.jwtService.generateToken(any())).thenReturn(token);

        final var result = this.authService.login(authRequest);

        verify(authenticationManager)
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        assertEquals(token, result);
    }
}