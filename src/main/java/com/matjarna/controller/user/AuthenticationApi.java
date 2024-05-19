package com.matjarna.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matjarna.dto.user.ForgotPasswordRequest;
import com.matjarna.dto.user.LoginRequest;
import com.matjarna.dto.user.ResetPasswordRequest;
import com.matjarna.facade.user.IUserFacade;

import jakarta.validation.Valid;

@RestController
@RequestMapping("")
public class AuthenticationApi {

	@Autowired
	private IUserFacade userFacade;

	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
		String authToken = userFacade.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
		if (authToken != null) {
			return ResponseEntity.ok(authToken);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
		}
	}

	@PostMapping(value = "/forgotPassword")
	public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
		userFacade.sendToken(forgotPasswordRequest);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/resetPassword")
	public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
		userFacade.resetPassword(resetPasswordRequest);
		return ResponseEntity.ok().build();
	}
}