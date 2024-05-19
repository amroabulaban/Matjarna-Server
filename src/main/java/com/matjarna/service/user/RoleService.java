package com.matjarna.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matjarna.model.roles.Role;
import com.matjarna.repository.user.IRoleRepository;

@Service
public class RoleService implements IRoleService {

	@Autowired
	private IRoleRepository roleRepository;

	@Override
	public boolean isEmpty() {
		return roleRepository.count() == 0;
	}

	@Override
	public void createRole(Role role) {
		roleRepository.save(role);
	}

	@Override
	public Role getRoleByName(String roleName) {
		Role role = roleRepository.findRoleByName(roleName);
		return role;
	}
}
