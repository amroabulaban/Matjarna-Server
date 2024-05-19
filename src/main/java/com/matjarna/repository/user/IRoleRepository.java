package com.matjarna.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matjarna.model.roles.Role;

public interface IRoleRepository extends JpaRepository<Role, Long> {

	Role findRoleByName(String roleName);
}
