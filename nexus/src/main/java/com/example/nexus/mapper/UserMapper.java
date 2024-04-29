package com.example.nexus.mapper;

import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = RoleMapper.class)
public interface UserMapper {
    @Mapping(target = "roles", qualifiedByName = "roleToString")
    UserResponse userToUserResponse(User user);
}