package com.matjarna.facade.user;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.matjarna.constants.Constants;
import com.matjarna.dto.EntityDtoList;
import com.matjarna.dto.SearchFilters;
import com.matjarna.dto.user.ChangePasswordRequest;
import com.matjarna.dto.user.ForgotPasswordRequest;
import com.matjarna.dto.user.RegisterRequest;
import com.matjarna.dto.user.ResetPasswordRequest;
import com.matjarna.dto.user.UserDto;
import com.matjarna.exception.ValidationException;
import com.matjarna.mapper.user.UserMapper;
import com.matjarna.model.user.ResetToken;
import com.matjarna.model.user.User;
import com.matjarna.model.user.UserData;
import com.matjarna.service.email.IEmailService;
import com.matjarna.service.user.IAuthenticationService;
import com.matjarna.service.user.IUserService;

@Service
public class UserFacade implements IUserFacade {

	@Autowired
	private IAuthenticationService authenticationService;

	@Autowired
	private IUserService userService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private IEmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TemplateEngine templateEngine;

	@Value("${fe.url}")
	private String feUrl;

	private final String RESET_LINK_PARAM = "resetLink";

	private final String DURATION_PARAM = "duration";

	private final String RESET_PASSWORD_URL = "/reset-password?token=";

	private final String RESET_PASSWORD_HTML_FILE = "ResetPassword";

	@Override
	public String loginUser(String email, String password) {
		return authenticationService.authenticate(email.toLowerCase(), password);
	}

	@Override
	public UserDto findByID(long id) {
		User user = userService.findByID(id);
		if (user != null) {
			UserDto userDto = userMapper.toUserDto(user);
			return userDto;
		} else {
			throw new ValidationException("User not found where id = " + id);
		}
	}

	@Override
	public UserDto createAdmin(RegisterRequest registerRequest) {
		if (registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
			User user = userMapper.toUser(registerRequest);
			UserDto mappedUser = userMapper.toUserDto(userService.saveAdmin(user));
			return mappedUser;
		} else {
			throw new ValidationException("Password do not match");
		}
	}

	@Override
	public UserDto createCustomer(RegisterRequest registerRequest) {
		if (registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
			User user = userMapper.toUser(registerRequest);
			UserDto mappedUser = userMapper.toUserDto(userService.saveCustomer(user));
			return mappedUser;
		} else {
			throw new ValidationException("Password do not match");
		}
	}

	@Override
	public EntityDtoList<UserDto, User> getUsers(Pageable pageable, SearchFilters searchTerm) {
		Page<User> page = userService.getUsers(pageable, searchTerm);
		EntityDtoList<UserDto, User> entityDtoList = new EntityDtoList<>(page, userMapper::toUserDto);
		return entityDtoList;
	}

	@Override
	public void changeUserState(long id, Boolean active) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserData user = (UserData) authentication.getPrincipal();
		long loggedId = user.getId();
		if (loggedId != id) {
			userService.changeUserState(id, active);
		} else {
			throw new ValidationException("You can't change your own state");
		}
	}

	@Override
	public void sendToken(ForgotPasswordRequest forgotPasswordRequest) {
		String email = forgotPasswordRequest.getEmail();
		User user = userService.findByEmail(email);
		if (user == null) {
			return;
		}

		String token = generateSecureToken();
		Calendar calender = Calendar.getInstance();
		calender.add(Calendar.MINUTE, Constants.RESET_TOKEN_EXPIRATION_MINUTES);
		Date experationDate = calender.getTime();

		ResetToken resetToken = new ResetToken();
		resetToken.setToken(token);
		resetToken.setExpiryDate(experationDate);
		user.setResetToken(resetToken);
		userService.saveAdmin(user);

		String resetPasswordLink = feUrl + RESET_PASSWORD_URL + token;
		Context context = new Context();
		context.setVariable(RESET_LINK_PARAM, resetPasswordLink);
		context.setVariable(DURATION_PARAM, Constants.RESET_TOKEN_EXPIRATION_MINUTES);
		String emailBody = templateEngine.process(RESET_PASSWORD_HTML_FILE, context);
		emailService.sendEmail(email, "Change Password", emailBody);
	}

	private String generateSecureToken() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] tokenBytes = new byte[32];
		secureRandom.nextBytes(tokenBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
	}

	@Override
	public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
		if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())) {
			throw new ValidationException("Passwords do not match");
		}
		User user = userService.findByResetToken(resetPasswordRequest.getToken());
		if (user == null) {
			throw new ValidationException("Invalid Token");
		}
		user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
		userService.saveAdmin(user);
	}

	public void changeUserPassword(long userId, ChangePasswordRequest changePasswordRequest) {
		if (changePasswordRequest.getPassword().equals(changePasswordRequest.getConfirmPassword())) {
			userService.changeUserPassword(userId, changePasswordRequest.getCurrentPassword(),
					changePasswordRequest.getPassword(), changePasswordRequest.getConfirmPassword());
		} else {
			throw new ValidationException("Passwords do not match");
		}
	}

	@Override
	public UserDto changeUserImage(long id, String imageUrl) {
		User user = userService.changeUserImage(id, imageUrl);
		UserDto mappedUser = userMapper.toUserDto(user);
		return mappedUser;
	}

	@Override
	public UserDto changeUserNames(long id, String firstName, String lastName) {
		User user = userService.changeUserNames(id, firstName, lastName);
		UserDto mappedUser = userMapper.toUserDto(user);
		return mappedUser;
	}

}