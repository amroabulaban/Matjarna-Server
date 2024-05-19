package com.matjarna.model.roles;

public enum Roles {

	ADMIN("ROLE_ADMIN"), USER("ROLE_USER"), CUSTOMER("ROLE_CUSTOMER");

	private String name;

	private Roles(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
