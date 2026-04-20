package com.bookinventory.user.controller;

import com.bookinventory.user.dto.PermRoleResponseDTO;
import com.bookinventory.user.repository.UserRepository;
import com.bookinventory.user.common.response.ApiResponse;
import com.bookinventory.user.service.PermRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class PermRoleController {

	private final PermRoleService permRoleService;
	private final UserRepository userRepository;

	public PermRoleController(PermRoleService permRoleService, UserRepository userRepository) {
		this.permRoleService = permRoleService;
		this.userRepository = userRepository;
	}

	/** GET /api/v1/roles — Public */
	@GetMapping
	public ResponseEntity<ApiResponse<List<PermRoleResponseDTO>>> getAllRoles() {
		return new ResponseEntity<>(
				ApiResponse.success(200, "Roles fetched successfully", permRoleService.getAllRoles()), HttpStatus.OK);
	}

	/** GET /api/v1/roles/{roleNumber} — Public */
	@GetMapping("/{roleNumber}")
	public ResponseEntity<ApiResponse<PermRoleResponseDTO>> getRoleById(@PathVariable Integer roleNumber) {

		return new ResponseEntity<>(
				ApiResponse.success(200, "Role fetched successfully", permRoleService.getRoleById(roleNumber)),
				HttpStatus.OK);
	}

	// GET /api/v1/roles/{roleNumber}/user-count
	// Public endpoint — shows how many users are in each role
	@GetMapping("/{roleNumber}/user-count")
	public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getUserCountByRole(
			@PathVariable Integer roleNumber) {

		// First check if role exists — throws 404 if not
		permRoleService.getRoleById(roleNumber);

		long count = userRepository.countByRole_RoleNumber(roleNumber);

		java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
		data.put("roleNumber", roleNumber);
		data.put("userCount", count);

		return ResponseEntity.ok(ApiResponse.success(200, "User count for role fetched successfully", data));
	}
}