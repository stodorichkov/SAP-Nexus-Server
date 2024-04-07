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
import java.util.ArrayList;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

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

        registerRequest = new RegisterRequest("Petar", "Georgiev", "petar_g",
                "123456aA", "123456aA");

        role = new Role();
        role.setName("USER");

        User user = new User();
        user.setId(1L);
        user.setUsername("petar_g");
        user.setRoles(new ArrayList<>());

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
                withMessage(MessageConstants.USER_ALREADY_EXISTS);

        verify(this.profileRepository, never()).save(any());
    }

    //will also show that incorrect format is of higher priority than incorrect repeated password
    @Test
    void registerUser_incorrectLengthPassword_expectUnauthorizedException() {
        //incorrect password format, as well as incorrect repeated password
        RegisterRequest wrongPasswordLengthRequest = new RegisterRequest("Petar", "Georgiev",
                "petar_g", "12345aA", "123456aA");
        when(userRepository.findByUsername("petar_g")).thenReturn(Optional.empty());

        assertThatExceptionOfType(UnauthorizedException.class).
                isThrownBy(() -> this.authService.registerUser(wrongPasswordLengthRequest)).
                withMessage(MessageConstants.WRONG_PASSWORD_FORMAT);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_noNumbersPassword_expectUnauthorizedException() {
        //incorrect password format, as well as incorrect repeated password
        RegisterRequest noNumbersPasswordRequest = new RegisterRequest("Petar", "Georgiev",
                "petar_g", "abcdABDC", "123456aA");
        when(userRepository.findByUsername("petar_g")).thenReturn(Optional.empty());

        assertThatExceptionOfType(UnauthorizedException.class).
                isThrownBy(() -> this.authService.registerUser(noNumbersPasswordRequest)).
                withMessage(MessageConstants.WRONG_PASSWORD_FORMAT);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_noUpperCaseLetterPassword_expectUnauthorizedException() {
        //incorrect password format, as well as incorrect repeated password
        RegisterRequest noUpperCaseLetterPasswordRequest = new RegisterRequest("Petar", "Georgiev",
                "petar_g", "abcd1234", "123456aA");
        when(userRepository.findByUsername("petar_g")).thenReturn(Optional.empty());

        assertThatExceptionOfType(UnauthorizedException.class).
                isThrownBy(() -> this.authService.registerUser(noUpperCaseLetterPasswordRequest)).
                withMessage(MessageConstants.WRONG_PASSWORD_FORMAT);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_noLowerCaseLetter_expectUnauthorizedException() {
        //incorrect password format, as well as incorrect repeated password
        RegisterRequest noLowerCaseLetterPasswordRequest = new RegisterRequest("Petar", "Georgiev",
                "petar_g", "ABCD1234", "123456aA");
        when(userRepository.findByUsername("petar_g")).thenReturn(Optional.empty());

        assertThatExceptionOfType(UnauthorizedException.class).
                isThrownBy(() -> this.authService.registerUser(noLowerCaseLetterPasswordRequest)).
                withMessage(MessageConstants.WRONG_PASSWORD_FORMAT);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_incorrectRepeatedPassword_expectUnauthorizedException() {
        RegisterRequest incorrectRepeatedPasswordRequest = new RegisterRequest("Petar", "Georgiev",
                "petar_g", "123456aA", "123456aB");
        when(userRepository.findByUsername("petar_g")).thenReturn(Optional.empty());

        assertThatExceptionOfType(UnauthorizedException.class).
                isThrownBy(() -> this.authService.registerUser(incorrectRepeatedPasswordRequest)).
                withMessage(MessageConstants.CONFIRM_PASSWORD_NOT_MATCHING);

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
    void registerUser_everythingIsCorrect_shouldSaveNewProfile() {
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