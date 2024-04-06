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

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTests {
    private static String username;
    private static String existingUsername;
    private static String correctPassword;
    private static String incorrectLengthPassword;
    private static String noNumbersPassword;
    private static String noUpperCaseLettersPassword;
    private static String noLowerCaseLettersPassword;
    private static String incorrectRepeatedPassword;
    private static String firstName;
    private static String lastName;
    private static String passwordHash;
    private static RegisterRequest registerRequest;
    private static Profile profile;
    private static Role role;
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
    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeAll
    static void setUp() {
        username = "petar_g";
        existingUsername = "ivan.m_22";
        correctPassword = "123456aA";
        incorrectLengthPassword = "12345aA";
        noNumbersPassword = "abcdABDC";
        noLowerCaseLettersPassword = "ABCD1234";
        noUpperCaseLettersPassword = "abcd1234";
        incorrectRepeatedPassword = "123456aB";
        firstName = "Petar";
        lastName = "Georgiev";
        passwordHash = "hashedPassword";
        role = new Role();
        role.setName("USER");
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setRoles(new ArrayList<>());
        profile = new Profile();
        profile.setId(1L);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setBalance(0.0f);
        profile.setUser(user);
    }
    @Test
    void registerUser_userAlreadyExists_throwException() {
        registerRequest = new RegisterRequest(firstName, lastName, existingUsername,
                correctPassword, correctPassword);
        when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(new User()));


        assertThatExceptionOfType(UserAlreadyExistsException.class).
                isThrownBy(() -> this.authService.registerUser(registerRequest)).
                withMessage(MessageConstants.USER_ALREADY_EXISTS);

        verify(this.profileRepository, never()).save(any());
    }

    //will also show that incorrect format is of higher priority than incorrect repeated password
    @Test
    void registerUser_incorrectLengthPassword_throwsException() {
        //incorrect password format, as well as incorrect repeated password
        registerRequest = new RegisterRequest(firstName, lastName, username,
                incorrectLengthPassword, correctPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UnauthorizedException.class).
                isThrownBy(() -> this.authService.registerUser(registerRequest)).
                withMessage(MessageConstants.WRONG_PASSWORD_FORMAT);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_noNumbersPassword_throwsException() {
        //incorrect password format, as well as incorrect repeated password
        registerRequest = new RegisterRequest(firstName, lastName, username,
                noNumbersPassword, correctPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UnauthorizedException.class).
                isThrownBy(() -> this.authService.registerUser(registerRequest)).
                withMessage(MessageConstants.WRONG_PASSWORD_FORMAT);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_noUpperCaseLetterPassword_throwsException() {
        //incorrect password format, as well as incorrect repeated password
        registerRequest = new RegisterRequest(firstName, lastName, username,
                noUpperCaseLettersPassword, correctPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UnauthorizedException.class).
                isThrownBy(() -> this.authService.registerUser(registerRequest)).
                withMessage(MessageConstants.WRONG_PASSWORD_FORMAT);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_noLowerCaseLetter_throwsException() {
        //incorrect password format, as well as incorrect repeated password
        registerRequest = new RegisterRequest(firstName, lastName, username,
                noLowerCaseLettersPassword, correctPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UnauthorizedException.class).
                isThrownBy(() -> this.authService.registerUser(registerRequest)).
                withMessage(MessageConstants.WRONG_PASSWORD_FORMAT);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_incorrectRepeatedPassword_throwsException() {
        registerRequest = new RegisterRequest(firstName, lastName, username,
                correctPassword, incorrectRepeatedPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UnauthorizedException.class).
                isThrownBy(() -> this.authService.registerUser(registerRequest)).
                withMessage(MessageConstants.CONFIRM_PASSWORD_NOT_MATCHING);

        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void registerUser_noRoleInDatabase_throwException() {
        registerRequest = new RegisterRequest(firstName, lastName, username,
                correctPassword, correctPassword);

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
        registerRequest = new RegisterRequest(firstName, lastName, username,
                correctPassword, correctPassword);

        when(registerMapper.mapProfile(registerRequest)).thenReturn(profile);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn(passwordHash);
        when(roleRepository.findByName(RoleConstants.USER)).
        thenReturn(Optional.of(role));

        this.authService.registerUser(registerRequest);

        final var profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(this.profileRepository).save(profileCaptor.capture());
        final var newProfile = profileCaptor.getValue();

        System.out.println(newProfile.getUser().getRoles());

        assertAll(
                () -> assertEquals(username, newProfile.getUser().getUsername()),
                () -> assertEquals(passwordHash, newProfile.getUser().getPassword()),
                () -> assertEquals(RoleConstants.USER, newProfile.getUser().getRoles().get(0).getName()),
                () -> assertEquals(firstName, newProfile.getFirstName()),
                () -> assertEquals(lastName, newProfile.getLastName()),
                () -> assertEquals(0.0f, newProfile.getBalance())
        );
    }
}