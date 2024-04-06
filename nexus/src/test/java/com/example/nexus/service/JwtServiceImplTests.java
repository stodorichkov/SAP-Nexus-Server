package com.example.nexus.service;

import com.example.nexus.constant.JwtConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTests {
    private static String username;
    private static Authentication authentication;

    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private JwtServiceImpl jwtService;

    @BeforeAll
    static void serUp() {
        username = "username";
        final  var authority = new SimpleGrantedAuthority("USER");
        authentication = new TestingAuthenticationToken(username, null, List.of(authority));
    }

    @Test
    void generateToken_invalidAuthentication_expectNullPointerException() {
        assertThrows(NullPointerException.class, () -> this.jwtService.generateToken(null));
    }

    @Test
    void generateToken_validAuthentication_expectToken() {
        final var token = this.jwtService.generateToken(authentication);

        assertNotNull(token);
    }

    @Test
    void getUsernameFromToken_invalidToken_expectIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> this.jwtService.getUsernameFromToken(null));
    }

    @Test
    void getUsernameFromToken_validToken_expectUsername() {
        final var token = this.jwtService.generateToken(authentication);
        final var result = this.jwtService.getUsernameFromToken(token);

        assertEquals(username, result);
    }

    @Test
    void validateToken_invalidToken_expectAuthenticationCredentialsNotFoundException() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.jwtService.validateToken(null));
    }

    @Test
    void validateToken_validToken_expectTrue() {
        final var token = this.jwtService.generateToken(authentication);

        assertTrue(this.jwtService.validateToken(token));
    }

    @Test
    void getTokenFromRequest_noToken_expectNull() {
        assertNull(this.jwtService.getTokenFromRequest(request));
    }

    @Test
    void getTokenFromRequest_noBarerToken_expectNull() {
        final var token = this.jwtService.generateToken(authentication);
        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER)).thenReturn(token);

        assertNull(this.jwtService.getTokenFromRequest(request));
    }

    @Test
    void getTokenFromRequest_BarerToken_expectToken() {
        final var token = this.jwtService.generateToken(authentication);
        when(request.getHeader(JwtConstants.AUTHORIZATION_HEADER)).thenReturn(JwtConstants.BEARER + token);

        assertEquals(token,this.jwtService.getTokenFromRequest(request));
    }
}