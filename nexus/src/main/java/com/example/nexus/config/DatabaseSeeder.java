package com.example.nexus.config;

import com.example.nexus.constant.CategoryConstants;
import com.example.nexus.constant.RoleConstants;
import com.example.nexus.service.CategoryService;
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
    private final CategoryService categoryService;

    @Override
    @Transactional
    public void run(String... args) {
        final var roles = ConstantsUtil.getAllConstants(RoleConstants.class);
        final var categories = ConstantsUtil.getAllConstants(CategoryConstants.class);

        roles.forEach(this.roleService::seedRole);
        categories.forEach(this.categoryService::seedCategory);
        this.userService.seedAdmin();
    }
}