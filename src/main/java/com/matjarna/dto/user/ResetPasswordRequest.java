package com.matjarna.dto.user;

import com.matjarna.constants.Constants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResetPasswordRequest {

	@NotBlank(message = "Password can not be blank")
	@Pattern(regexp = Constants.REGEXP_PASSWORD, message = "Password must be at least 6 characters and contain at least one number and one character.")
	private String password;

	@NotBlank(message = "Confirm password can not be blank")
	private String confirmPassword;

	@NotBlank(message = "Token can not be blank!")
	private String token;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
