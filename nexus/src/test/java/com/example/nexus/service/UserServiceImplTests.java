package com.example.nexus.service;

import com.example.nexus.constant.AdminConstants;
import com.example.nexus.constant.RoleConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.UserMapper;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.Role;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.response.UserResponse;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.RoleRepository;
import com.example.nexus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("USER");

        user = new User();
        user.setId(1L);
        user.setUsername("Username");
        user.setPassword("Password");
        user.getRoles().add(role);

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
    public void addUserRole_userNotExist_expectNotFoundException() {
        when(this.userRepository.findByUsername("Username")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.addUserRole("Username"));
    }

    @Test
    public void addUserRole_roleNotExist_expectNotFoundException() {
        when(this.userRepository.findByUsername("Username")).thenReturn(Optional.of(new User()));
        when(this.roleRepository.findByName(RoleConstants.ADMIN)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.addUserRole("Username"));
    }

    @Test
    public void addUserRole_userAlreadyHasRole_expectDoNothing() {
        //this will change role already in user list because it serves as a pointer.
        role.setName("ADMIN");
        role.setId(2L);

        when(this.userRepository.findByUsername("Username")).thenReturn(Optional.of(user));
        when(this.roleRepository.findByName(RoleConstants.ADMIN)).thenReturn(Optional.of(role));

        this.userService.addUserRole("Username");

        verify(this.userRepository, never()).save(any());
    }

    @Test
    public void addUSerRole_everythingIsFine_expectToWork() {
        Role newRole = new Role();
        newRole.setName("ADMIN");

        when(this.userRepository.findByUsername("Username")).thenReturn(Optional.of(user));
        when(this.roleRepository.findByName(RoleConstants.ADMIN)).thenReturn(Optional.of(newRole));

        this.userService.addUserRole("Username");

        verify(this.userRepository).save(userCaptor.capture());
        assertEquals(user, userCaptor.getValue());
    }

    @Test
    public void removeUserRole_userNotExist_expectNotFoundException() {
        when(this.userRepository.findByUsername("Username")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.removeUserRole("Username"));
    }

    @Test
    public void removeUserRole_roleNotExist_expectNotFoundException() {
        when(this.userRepository.findByUsername("Username")).thenReturn(Optional.of(new User()));
        when(this.roleRepository.findByName(RoleConstants.ADMIN)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.removeUserRole("Username"));
    }

    @Test
    public void removeUserRole_userAlreadyHasRole_expectDoNothing() {
        Role newRole = new Role();
        newRole.setName("ADMIN");

        when(this.userRepository.findByUsername("Username")).thenReturn(Optional.of(user));
        when(this.roleRepository.findByName(RoleConstants.ADMIN)).thenReturn(Optional.of(newRole));

        this.userService.removeUserRole("Username");

        verify(this.userRepository, never()).save(any());
    }

    @Test
    public void removeUSerRole_everythingIsFine_expectToWork() {
        Role newRole = new Role();
        newRole.setName("ADMIN");
        user.getRoles().add(newRole);

        when(this.userRepository.findByUsername("Username")).thenReturn(Optional.of(user));
        when(this.roleRepository.findByName(RoleConstants.ADMIN)).thenReturn(Optional.of(newRole));

        this.userService.removeUserRole("Username");

        user.getRoles().remove(newRole);

        verify(this.userRepository).save(userCaptor.capture());
        assertEquals(user, userCaptor.getValue());
    }
}