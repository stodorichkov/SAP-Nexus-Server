package com.example.nexus.service;

import com.example.nexus.constant.AdminConstants;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {
    private static String passwordHash;
    private static List<Role> roles;
    private static Page<User> page;
    private static UserResponse userResponse;
    private static Pageable pageable;

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
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeAll
    static void serUp() {
        passwordHash = "hashedPassword";

        roles = new ArrayList<>();
        Role role = new Role();
        role.setId(1L);
        role.setName("USER");
        roles.add(role);
        role.setId(2L);
        role.setName("ADMIN");
        roles.add(role);

        final var users = List.of(new User(), new User(), new User());
        page = new PageImpl<>(users);

        pageable = Pageable.ofSize(20);

        userResponse = new UserResponse("stodorichkov123", roles.stream().map(Role::getName).toList());
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
        when(this.roleRepository.findAll()).thenReturn(roles);
        when(this.passwordEncoder.encode(AdminConstants.PASSWORD)).thenReturn(passwordHash);

        this.userService.seedAdmin();

        final var profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(this.profileRepository).save(profileCaptor.capture());
        final var profile = profileCaptor.getValue();

        assertAll(
                () -> assertEquals(AdminConstants.USERNAME, profile.getUser().getUsername()),
                () -> assertEquals(passwordHash, profile.getUser().getPassword()),
                () -> assertEquals(roles, profile.getUser().getRoles()),
                () -> assertEquals(AdminConstants.FIRST_NAME, profile.getFirstName()),
                () -> assertEquals(AdminConstants.LAST_NAME, profile.getLastName())
        );
    }

    @Test
    public void getUsers_expectPage() {
        when(this.userRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(this.userMapper.userToUserResponse(any(User.class))).thenReturn(userResponse);

        final var result = this.userService.getUsers(pageable);

        assertNotNull(result);
        assertEquals(page.getContent().size(), result.getContent().size());
    }
}