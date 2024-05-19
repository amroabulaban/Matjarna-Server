package com.matjarna.facade.user;

import org.springframework.data.domain.Pageable;

import com.matjarna.dto.EntityDtoList;
import com.matjarna.dto.SearchFilters;
import com.matjarna.dto.user.ChangePasswordRequest;
import com.matjarna.dto.user.ForgotPasswordRequest;
import com.matjarna.dto.user.RegisterRequest;
import com.matjarna.dto.user.ResetPasswordRequest;
import com.matjarna.dto.user.UserDto;
import com.matjarna.model.user.User;

public interface IUserFacade {

	String loginUser(String email, String password);

	UserDto findByID(long id);

	UserDto createAdmin(RegisterRequest registerRequest);

	UserDto createCustomer(RegisterRequest registerRequest);

	EntityDtoList<UserDto, User> getUsers(Pageable pageable, SearchFilters searchTerm);

	void changeUserState(long id, Boolean active);

	void sendToken(ForgotPasswordRequest forgotPasswordRequest);

	void resetPassword(ResetPasswordRequest resetPasswordRequest);

	void changeUserPassword(long userId, ChangePasswordRequest changePasswordRequest);

	UserDto changeUserImage(long id, String imageUrl);

	UserDto changeUserNames(long id, String firstName, String lastName);

}