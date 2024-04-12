package com.example.nexus.mapper;

import com.example.nexus.model.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    @Mapping(target = "role", source = "name")
    SimpleGrantedAuthority roleToSimpleGrantedAuthority(Role role);

    @Named("roleToString")
    static String roleToString(Role role) {
        return role.getName();
    }
}