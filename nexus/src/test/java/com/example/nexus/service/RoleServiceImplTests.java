package com.example.nexus.service;

import com.example.nexus.model.entity.Role;
import com.example.nexus.repository.RoleRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTests {
    private static String roleName;

    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeAll
    static void serUp() {
        roleName = "New Role";
    }

    @Test
    void seedRole_roleNotExist_expectSave() {
        this.roleService.seedRole(roleName);

        verify(this.roleRepository, times(1))
                .save(argThat(role -> role.getName().equals(roleName)));
    }

    @Test
    void seedRole_roleAlreadyExist_expectNotSave() {
        when(this.roleRepository.findByName(roleName)).thenReturn(Optional.of(new Role()));

        this.roleService.seedRole(roleName);

        verify(this.roleRepository, never()).save(any());
    }
}