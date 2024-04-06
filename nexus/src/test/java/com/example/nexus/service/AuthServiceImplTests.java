package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.UserAlreadyExistsException;
import com.example.nexus.mapper.RegisterMapper;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.RegisterRequest;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.RoleRepository;
import com.example.nexus.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
    private static RegisterRequest registerRequest;
    @Mock
    private AuthenticationManager authenticationManager;
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
    }
    @Test
    void registerUser_userAlreadyExists_throwException() {
        registerRequest = new RegisterRequest(firstName, lastName, existingUsername, correctPassword, correctPassword);
        when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(new User()));


        assertThatExceptionOfType(UserAlreadyExistsException.class).
                isThrownBy(() -> this.authService.registerUser(registerRequest)).
                withMessage(MessageConstants.USER_ALREADY_EXISTS);

        verify(this.profileRepository, never()).save(any());
    }
}