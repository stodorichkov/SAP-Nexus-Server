package com.example.nexus.service;

import com.example.nexus.constant.AdminConstants;
import com.example.nexus.constant.MessageConstants;
import com.example.nexus.constant.PageConstants;
import com.example.nexus.constant.RoleConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.UserMapper;
import com.example.nexus.model.entity.Profile;
import com.example.nexus.model.entity.Role;
import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.RoleUpdateRequest;
import com.example.nexus.model.payload.response.UserResponse;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.RoleRepository;
import com.example.nexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public void seedAdmin() {
        if(userRepository.findByUsername(AdminConstants.USERNAME).isPresent()) {
            return;
        }

        final var roles = this.roleRepository.findAll();
        final var user = new User();
        final var profile = new Profile();

        user.setUsername(AdminConstants.USERNAME);
        user.setPassword(passwordEncoder.encode(AdminConstants.PASSWORD));
        user.setRoles(roles);
        profile.setUser(user);
        profile.setFirstName(AdminConstants.FIRST_NAME);
        profile.setLastName(AdminConstants.LAST_NAME);

        this.profileRepository.save(profile);
    }

    @Override
    public Page<UserResponse> getUsers(int pageNumber) {
        final var pageable = PageRequest.of(pageNumber, PageConstants.USER_PAGE_SIZE);

        return this.profileRepository
                .findAll(pageable)
                .map(this.userMapper::map);
    }

    @Override
    public void addUserRole(RoleUpdateRequest request) {
        final var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new NotFoundException(MessageConstants.USER_NOT_FOUND));

        if(user.getRoles().stream().noneMatch(role -> role.getName()
                .equals(RoleConstants.ADMIN))) {
            return;
        }

        Role newRole = new Role();
        newRole.setName(request.roleName());
        List<Role> userRoles = user.getRoles();
        userRoles.add(newRole);
        user.setRoles(userRoles);

        userRepository.save(user);
    }

    @Override
    public void removeUserRole(RoleUpdateRequest request) {
        final var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new NotFoundException(MessageConstants.USER_NOT_FOUND));
        user.getRoles().removeIf(role -> role.getName().equals(request.roleName()));
        userRepository.save(user);
    }
}