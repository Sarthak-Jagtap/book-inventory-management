package com.bookinventory.user.service;

import com.bookinventory.user.dto.ChangePasswordRequestDTO;
import com.bookinventory.user.dto.LoginRequestDTO;
import com.bookinventory.user.dto.LoginResponseDTO;
import com.bookinventory.user.dto.UserRequestDTO;
import com.bookinventory.user.dto.UserResponseDTO;
import com.bookinventory.user.dto.UserUpdateRequestDTO;

import java.util.List;

public interface UserService {

    // Register a new user
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);

    // Login
    LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO);

    // Get user by ID
    UserResponseDTO getUserById(Integer userId);

    // Get user by username
    UserResponseDTO getUserByUsername(String userName);

    // Get all users
    List<UserResponseDTO> getAllUsers();

    // Get all users by role
    List<UserResponseDTO> getUsersByRole(Integer roleNumber);

    // Update user profile (name, phone, username)
    UserResponseDTO updateUser(Integer userId, UserUpdateRequestDTO updateDTO);

    // Change password
    void changePassword(Integer userId, ChangePasswordRequestDTO changePasswordDTO);

    // Assign / change role of a user
    UserResponseDTO updateUserRole(Integer userId, Integer roleNumber);

    // Delete user
    void deleteUser(Integer userId);
}