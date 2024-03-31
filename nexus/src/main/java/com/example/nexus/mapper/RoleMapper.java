package com.example.nexus.mapper;

import com.example.nexus.model.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.security.core.GrantedAuthority;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    @Mapping(target = "authority", source = "name")
    GrantedAuthority toGrantedAuthority(Role entity);
}