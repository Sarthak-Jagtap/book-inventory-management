package com.bookinventory.user.controller;

import com.bookinventory.user.dto.*;
import com.bookinventory.user.response.ApiResponse;
import com.bookinventory.user.service.PermRoleService;
import com.bookinventory.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

	private final UserService userService;
	private final PermRoleService permRoleService;

	public AdminController(UserService userService, PermRoleService permRoleService) {
		this.userService = userService;
		this.permRoleService = permRoleService;
	}

	// USER MANAGEMENT
	@GetMapping("/users")
	public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
		List<UserResponseDTO> users = userService.getAllUsers();
		return new ResponseEntity<>(ApiResponse.success(200, "All users fetched successfully", users), HttpStatus.OK);
	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Integer userId) {

		UserResponseDTO user = userService.getUserById(userId);
		return new ResponseEntity<>(ApiResponse.success(200, "User fetched successfully", user), HttpStatus.OK);
	}

	@PatchMapping("/users/{userId}")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable Integer userId,
			@Valid @RequestBody UserUpdateRequestDTO dto) {

		UserResponseDTO updated = userService.updateUserById(userId, dto);
		return new ResponseEntity<>(ApiResponse.success(200, "User updated successfully", updated), HttpStatus.OK);
	}

	@PatchMapping("/users/{userId}/role")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserRole(@PathVariable Integer userId,
			@RequestBody RoleUpdateRequestDTO dto) {

		UserResponseDTO updated = userService.updateUserRole(userId, dto.getRoleNumber());
		return new ResponseEntity<>(ApiResponse.success(200, "User role updated successfully", updated), HttpStatus.OK);
	}
	
	// Add PUT /admin/users/{userId}
	@PutMapping("/users/{userId}")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserPut(
	        @PathVariable Integer userId,
	        @Valid @RequestBody UserUpdateRequestDTO dto) {
	    UserResponseDTO updated = userService.updateUserById(userId, dto);
	    return new ResponseEntity<>(
	        ApiResponse.success(200, "User updated successfully", updated), HttpStatus.OK);
	}

	// Add PUT /admin/users/{userId}/role
	@PutMapping("/users/{userId}/role")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserRolePut(
	        @PathVariable Integer userId,
	        @RequestBody RoleUpdateRequestDTO dto) {
	    UserResponseDTO updated = userService.updateUserRole(userId, dto.getRoleNumber());
	    return new ResponseEntity<>(
	        ApiResponse.success(200, "User role updated successfully", updated), HttpStatus.OK);
	}

	// ROLE MANAGEMENT

	@GetMapping("/roles")
	public ResponseEntity<ApiResponse<List<PermRoleResponseDTO>>> getAllRoles() {
		List<PermRoleResponseDTO> roles = permRoleService.getAllRoles();
		return new ResponseEntity<>(ApiResponse.success(200, "Roles fetched successfully", roles), // ✅ FIXED
				HttpStatus.OK);
	}

	@GetMapping("/roles/{roleNumber}")
	public ResponseEntity<ApiResponse<PermRoleResponseDTO>> getRoleById(@PathVariable Integer roleNumber) {

		PermRoleResponseDTO role = permRoleService.getRoleById(roleNumber);
		return new ResponseEntity<>(ApiResponse.success(200, "Role fetched successfully", role), // ✅ FIXED
				HttpStatus.OK);
	}
	
	/*
	 * Delete user feature not needed
	 * 
	 * // DELETE /api/v1/admin/users/{userId} — soft delete (sets active = false)
	 * 
	 * @DeleteMapping("/users/{userId}") public
	 * ResponseEntity<ApiResponse<UserResponseDTO>> deleteUser(
	 * 
	 * @PathVariable Integer userId) { UserResponseDTO updated =
	 * userService.updateUserStatus(userId, false); return new ResponseEntity<>(
	 * ApiResponse.success(200, "User deactivated successfully", updated),
	 * HttpStatus.OK); }
	 */
}