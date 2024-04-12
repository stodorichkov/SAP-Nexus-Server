package com.example.nexus.service;

import com.example.nexus.constant.AdminConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.UserMapper;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.Role;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.response.UserResponse;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.RoleRepository;
import com.example.nexus.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {
    private static User user;
    private static Role role;
    private static UserResponse userResponse;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Captor
    private ArgumentCaptor<Profile> profileCaptor;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeAll
    static void serUp() {
        role = new Role();
        role.setId(1L);
        role.setName("USER");

        user = new User();
        user.setId(1L);
        user.setUsername("Username");
        user.setPassword("Password");
        user.setRoles(List.of(role));

        userResponse = new UserResponse(
                "Username",
                List.of("USER")
        );
    }

    @Test
    void seedAdmin_userAlreadyExists_expectNotSave() {
        when(this.userRepository.findByUsername(AdminConstants.USERNAME)).thenReturn(Optional.of(new User()));

        this.userService.seedAdmin();

        verify(this.userRepository, never()).save(any());
        verify(this.profileRepository, never()).save(any());
    }

    @Test
    void seedAdmin_userNotExists_expectSave() {
        when(this.roleRepository.findAll()).thenReturn(user.getRoles());
        when(this.passwordEncoder.encode(AdminConstants.PASSWORD)).thenReturn(user.getPassword());

        this.userService.seedAdmin();

        verify(this.profileRepository).save(profileCaptor.capture());

        assertAll(
                () -> assertEquals(AdminConstants.USERNAME, profileCaptor.getValue().getUser().getUsername()),
                () -> assertEquals(user.getPassword(), profileCaptor.getValue().getUser().getPassword()),
                () -> assertEquals(user.getRoles(), profileCaptor.getValue().getUser().getRoles()),
                () -> assertEquals(AdminConstants.FIRST_NAME, profileCaptor.getValue().getFirstName()),
                () -> assertEquals(AdminConstants.LAST_NAME, profileCaptor.getValue().getLastName())
        );
    }

    @Test
    public void getUsers_expectPage() {
        final var userPage = new PageImpl<>(List.of(user));
        final var pageable = Pageable.unpaged();

        when(this.userRepository.findAll(eq(pageable))).thenReturn(userPage);
        when(this.userMapper.userToUserResponse(any(User.class))).thenReturn(userResponse);

        final var result = this.userService.getUsers(pageable);

        assertEquals(List.of(userResponse), result.getContent());
    }

    @Test
    public void updateUserRole_userNotExist_expectNotFoundException() {
        when(this.userRepository.findByUsername("stodorichkov123"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUserRole(userResponse));
    }

    @Test
    public void updateUserRole_userExists_expectUpdate() {
        roles.remove(roles.size() - 1);
        User user = new User();
        user.setUsername("stodorichkov");
        user.setPassword("password");
        user.setRoles(roles);
        when(this.passwordEncoder.encode("password"))
                .thenReturn(passwordHash);
        when(this.userRepository.findByUsername("stodorichkov123"))
                .thenReturn(Optional.of(user));

        final var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(this.userRepository).save(userCaptor.capture());
        final var userValue = userCaptor.getValue();

        assertAll(
                () -> assertEquals("stodorichkov", userValue.getUsername()),
                () -> assertEquals(passwordHash, userValue.getPassword()),
                () -> assertEquals(roles, userValue.getRoles())
        );
    }
}