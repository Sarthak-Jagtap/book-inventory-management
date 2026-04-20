package com.bookinventory.user.controller;

import com.bookinventory.user.common.response.ApiResponse;
import com.bookinventory.user.dto.*;

import com.bookinventory.user.service.PermRoleService;
import com.bookinventory.user.service.PurchaseLogService;
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
	private final PurchaseLogService purchaseLogService;

	public AdminController(UserService userService, PermRoleService permRoleService, PurchaseLogService purchaseLogService) {
		this.userService = userService;
		this.permRoleService = permRoleService;
		this.purchaseLogService = purchaseLogService;
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
	
	// GET /api/v1/admin/users/search?firstName=John
	// GET /api/v1/admin/users/search?lastName=Smith
	// GET /api/v1/admin/users/search?firstName=John&lastName=Smith
	// Leverages UserRepository.findByFirstNameIgnoreCase and findByLastNameIgnoreCase
	// which already exist but were never exposed via an endpoint
	@GetMapping("/users/search")
	public ResponseEntity<ApiResponse<List<UserResponseDTO>>> searchUsers(
	        @RequestParam(required = false) String firstName,
	        @RequestParam(required = false) String lastName) {

	    if ((firstName == null || firstName.isBlank()) &&
	        (lastName  == null || lastName.isBlank())) {
	        return ResponseEntity.badRequest()
	            .body(ApiResponse.failure(400,
	                "Provide at least one search param: firstName or lastName"));
	    }

	    List<UserResponseDTO> results = userService.searchUsers(firstName, lastName);
	    return ResponseEntity.ok(
	        ApiResponse.success(200, "Search results fetched successfully", results));
	}
	
	// GET /api/v1/admin/users/by-role/{roleNumber}
	// Example: /admin/users/by-role/2 → all RegisteredUsers
	// getUsersByRole() already existed in UserService — this just exposes it
	@GetMapping("/users/by-role/{roleNumber}")
	public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsersByRole(
	        @PathVariable Integer roleNumber) {

	    List<UserResponseDTO> users = userService.getUsersByRole(roleNumber);
	    return ResponseEntity.ok(
	        ApiResponse.success(200, "Users by role fetched successfully", users));
	}
	
	// Also inject these — add to AdminController constructor:
	// private final PurchaseLogService purchaseLogService;
	// private final PermRoleService permRoleService;

	// GET /api/v1/admin/dashboard
	// Master overview for admin: total users, breakdown by role, total purchases
	@GetMapping("/dashboard")
	public ResponseEntity<ApiResponse<AdminDashboardDTO>> getAdminDashboard() {

	    // 1. Total user count
	    long totalUsers = userService.getAllUsers().size();

	    // 2. Total purchases
	    long totalPurchases = purchaseLogService.getAllPurchases().size();

	    // 3. Users per role — loop through all roles
	    java.util.Map<String, Long> countByRole = new java.util.LinkedHashMap<>();
	    for (PermRoleResponseDTO role : permRoleService.getAllRoles()) {
	        long count = userService.getUsersByRole(role.getRoleNumber()).size();
	        countByRole.put(role.getPermRole(), count);
	    }

	    // 4. Build dashboard
	    AdminDashboardDTO dashboard = new AdminDashboardDTO();
	    dashboard.setTotalUsers(totalUsers);
	    dashboard.setTotalPurchases(totalPurchases);
	    dashboard.setUserCountByRole(countByRole);

	    return ResponseEntity.ok(
	        ApiResponse.success(200, "Admin dashboard fetched successfully", dashboard));
	}
}