package com.matjarna.service.user;

import com.matjarna.model.roles.Role;

public interface IRoleService {

	public boolean isEmpty();

	public void createRole(Role role);

	public Role getRoleByName(String roleName);

}
