package com.matjarna.mapper.user;

import org.mapstruct.Mapper;

import com.matjarna.dto.user.RoleDto;
import com.matjarna.model.roles.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
	RoleDto toRoleDto(Role role);

	Role toRole(RoleDto role);
}
