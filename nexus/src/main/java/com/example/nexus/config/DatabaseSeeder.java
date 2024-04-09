package com.example.nexus.config;

import com.example.nexus.constant.RoleConstants;
import com.example.nexus.service.RoleService;
import com.example.nexus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final RoleService roleService;
    private final UserService userService;

    @Override
    @Transactional
    public void run(String... args) {
        final var roles = ConstantsUtil.getAllConstants(RoleConstants.class);

        roles.forEach(this.roleService::seedRole);
        this.userService.seedAdmin();
    }
}