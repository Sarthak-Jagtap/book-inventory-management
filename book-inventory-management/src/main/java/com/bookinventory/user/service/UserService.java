package com.bookinventory.user.service;

import com.bookinventory.user.dto.*;

import java.util.List;

public interface UserService {

	// Auth (Guest)
	UserResponseDTO registerUser(UserRequestDTO dto);

	LoginResponseDTO loginUser(LoginRequestDTO dto);

	// Own profile (RegisteredUser — userId comes from JWT)
	UserResponseDTO getMyProfile(Integer userId);

	UserResponseDTO updateMyProfile(Integer userId, UserUpdateRequestDTO dto);

	void changeMyPassword(Integer userId, ChangePasswordRequestDTO dto);

	// Admin — manage all users
	List<UserResponseDTO> getAllUsers(); // all (active + inactive)
	
	UserResponseDTO getUserById(Integer userId);

	UserResponseDTO updateUserById(Integer userId, UserUpdateRequestDTO dto);

	UserResponseDTO updateUserRole(Integer userId, Integer roleNumber);
	
	
	// Shared utility
	List<UserResponseDTO> getUsersByRole(Integer roleNumber);
	
	List<UserResponseDTO> searchUsers(String firstName, String lastName);
}