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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTests {
    private static Role role;

    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeAll
    static void setUp() {
        role = new Role();
        role.setName("Role");
    }

    @Test
    void seedRole_roleNotExist_expectSave() {
        this.roleService.seedRole(role.getName());

        verify(this.roleRepository, times(1)).save(role);
    }

    @Test
    void seedRole_roleAlreadyExist_expectNotSave() {
        when(this.roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));

        this.roleService.seedRole(role.getName());

        verify(this.roleRepository, never()).save(role);
    }
}