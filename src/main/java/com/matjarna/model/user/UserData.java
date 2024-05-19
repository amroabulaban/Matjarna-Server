package com.matjarna.model.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserData implements UserDetails {

	private static final long serialVersionUID = 1L;

	private long id;

	private String email;

	private String password;

	private Set<String> roleNames = new HashSet<>();

	private boolean active;

	public UserData(long id, String email, Set<String> roleNames) {
		this.id = id;
		this.email = email;
		this.roleNames = roleNames;
	}

	public UserData(long id, String email) {
		this.id = id;
		this.email = email;
	}

	public UserData(User user) {
		id = user.getId();
		email = user.getEmail();
		password = user.getPassword();
		user.getRoles().forEach(role -> roleNames.add(role.getName()));
		active = user.isActive();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<>();
		roleNames.forEach(roleName -> authorities.add(new SimpleGrantedAuthority(roleName)));
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	public long getId() {
		return id;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	public Set<String> getRoleNames() {
		return roleNames;
	}
}