package com.example.nexus.service;

import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.RoleMapper;
import com.example.nexus.model.entity.Role;
import com.example.nexus.model.entity.User;
import com.example.nexus.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTests {
    private static String username;
    private static String password;
    private static User user;
    private static Role role;
    private static SimpleGrantedAuthority authority;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleMapper roleMapper;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeAll
    static void setUp() {
        username = "user";
        password = "password";

        role= new Role();
        role.setName("USER");

        user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.getRoles().add(role);

        authority = new SimpleGrantedAuthority("USER");
    }

    @Test
    void loadUserByUsername_userNotExist_expectNotFoundException() {
        assertThrows(NotFoundException.class, () -> this.userDetailsService.loadUserByUsername(username));
    }

    @Test
    void loadUserByUsername_userExist_expectLoadUser() {
        when(this.userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(this.roleMapper.roleToSimpleGrantedAuthority(role)).thenReturn(authority);

        final var userDetails = this.userDetailsService.loadUserByUsername(username);

        assertAll(
                () -> assertEquals(username, userDetails.getUsername()),
                () -> assertEquals(password, userDetails.getPassword()),
                () -> assertEquals(Set.of(authority), userDetails.getAuthorities())
        );
    }
}