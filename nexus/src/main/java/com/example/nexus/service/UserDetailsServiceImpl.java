package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.RoleMapper;
import com.example.nexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       final var user = this.userRepository
               .findByUsername(username)
               .orElseThrow(() -> new NotFoundException(MessageConstants.USER_NOT_FOUND));

       final var authorities = user
               .getRoles()
               .stream()
               .map(roleMapper::toGrantedAuthority)
               .toList();

       return new User(user.getUsername(), user.getPassword(), authorities);
    }
}