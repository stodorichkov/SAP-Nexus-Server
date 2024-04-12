package com.example.nexus.mapper;

import com.example.nexus.model.entity.User;
import com.example.nexus.model.payload.request.RoleUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleUpdateMapper {
    User roleUpdateRequestToUser(RoleUpdateRequest request);
}
