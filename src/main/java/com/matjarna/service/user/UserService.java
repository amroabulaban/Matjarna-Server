package com.matjarna.service.user;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matjarna.dto.SearchFilters;
import com.matjarna.exception.DuplicateUserException;
import com.matjarna.exception.ServiceException;
import com.matjarna.exception.ValidationException;
import com.matjarna.mapper.user.UserMapper;
import com.matjarna.model.roles.Role;
import com.matjarna.model.roles.Roles;
import com.matjarna.model.user.User;
import com.matjarna.model.user.UserData;
import com.matjarna.repository.user.IUserRepository;
import com.matjarna.service.image.IImageService;

@Service
public class UserService implements IUserService, UserDetailsService {

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private IImageService imageService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	UserMapper userMapper;

	@Override
	public User saveAdmin(User user) {
		if (user.getId() == null && userRepository.findByEmail(user.getEmail()) != null) {
			throw new DuplicateUserException("User with email " + user.getEmail() + " already exists.");
		}
		Set<Role> adminRole = new HashSet<>();
		Role role = roleService.getRoleByName(Roles.ADMIN.getName());
		adminRole.add(role);
		user.setRoles(adminRole);

		return userRepository.save(user);
	}

	@Override
	public User saveCustomer(User user) {
		if (user.getId() == null && userRepository.findByEmail(user.getEmail()) != null) {
			throw new DuplicateUserException("User with email " + user.getEmail() + " already exists.");
		}
		Set<Role> customerRole = new HashSet<>();
		Role role = roleService.getRoleByName(Roles.CUSTOMER.getName());
		customerRole.add(role);
		user.setRoles(customerRole);

		return userRepository.save(user);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User findByID(long id) {
		return userRepository.findByid(id);
	}

	@Override
	public Page<User> getUsers(Pageable pageable, SearchFilters searchTerm) {
		Page<User> page = userRepository.getUsers(pageable, searchTerm.isActive(), searchTerm.getSearchTerm());
		return page;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email.toLowerCase());
		if (user == null) {
			throw new UsernameNotFoundException("User not found with email: " + email);
		}
		return new UserData(user);
	}

	@Override
	public void changeUserState(long id, Boolean active) {
		User user = userRepository.findByid(id);
		if (user == null) {
			throw new ServiceException("User not Found!");
		} else {
			user.setActive(active);
			userRepository.save(user);
		}
	}

	@Override
	public User findByResetToken(String token) {
		return userRepository.findByResetToken(token, new Date());
	}

	public void changeUserPassword(long userId, String currentPassword, String password, String confirmPassword) {
		User user = userRepository.findByid(userId);
		if (user == null) {
			throw new ServiceException("User not Found!");
		} else {
			String oldPassword = user.getPassword();
			if (passwordEncoder.matches(password, oldPassword)) {
				throw new ValidationException("Your new password must be different than your old one!");
			}
			if (passwordEncoder.matches(currentPassword, oldPassword)) {
				user.setPassword(passwordEncoder.encode(password));
				userRepository.save(user);
			} else {
				throw new ValidationException("You entered a wrong password!");
			}
		}
	}

	public User changeUserImage(long id, String imageUrl) {
		User user = userRepository.findByid(id);
		if (user == null) {
			throw new ServiceException("User not Found!");
		} else {
			String oldImageUrl = user.getProfilePicture();
			user.setProfilePicture(imageUrl);
			userRepository.save(user);
			if (oldImageUrl != null) {
				imageService.deleteImage(oldImageUrl);
			}
			return user;
		}
	}

	@Override
	public User changeUserNames(long id, String firstName, String lastName) {
		User user = userRepository.findByid(id);
		if (user == null) {
			throw new ServiceException("User not Found!");
		} else {
			user.setFirstName(firstName);
			user.setLastName(lastName);
			userRepository.save(user);
		}
		return user;
	}
}