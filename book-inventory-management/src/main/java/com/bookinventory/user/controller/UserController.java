package com.bookinventory.user.controller;

import com.bookinventory.common.exception.ForbiddenException;
import com.bookinventory.user.common.response.ApiResponse;
import com.bookinventory.user.dto.*;
import com.bookinventory.user.service.PurchaseLogService;
import com.bookinventory.user.service.UserService;
import com.bookinventory.user.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	private final UserService userService;
	private final PurchaseLogService purchaseLogService;
	private final JwtUtil jwtUtil;

	public UserController(UserService userService, PurchaseLogService purchaseLogService, JwtUtil jwtUtil) {
		this.userService = userService;
		this.purchaseLogService = purchaseLogService;
		this.jwtUtil = jwtUtil;
	}

	private Integer extractUserId(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		String token = authHeader.substring(7);
		return jwtUtil.extractUserId(token);
	}

	// GET PROFILE
	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<UserResponseDTO>> getMyProfile(HttpServletRequest request) {

		Integer userId = extractUserId(request);
		UserResponseDTO user = userService.getMyProfile(userId);

		return new ResponseEntity<>(ApiResponse.success(200, "Profile fetched successfully", user), HttpStatus.OK);
	}

	// UPDATE PROFILE
	@PatchMapping("/profile")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateMyProfile(HttpServletRequest request,
			@Valid @RequestBody UserUpdateRequestDTO dto) {

		Integer userId = extractUserId(request);
		UserResponseDTO updated = userService.updateMyProfile(userId, dto);

		return new ResponseEntity<>(ApiResponse.success(200, "Profile updated successfully", updated), HttpStatus.OK);
	}

	// Keep the existing PATCH, add PUT alongside it
	@PutMapping("/profile")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateMyProfilePut(HttpServletRequest request,
			@Valid @RequestBody UserUpdateRequestDTO dto) {
		Integer userId = extractUserId(request);
		UserResponseDTO updated = userService.updateMyProfile(userId, dto);
		return new ResponseEntity<>(ApiResponse.success(200, "Profile updated successfully", updated), HttpStatus.OK);
	}

	// CHANGE PASSWORD
	@PatchMapping("/change-password")
	public ResponseEntity<ApiResponse<Object>> changePassword(HttpServletRequest request,
			@Valid @RequestBody ChangePasswordRequestDTO dto) {

		Integer userId = extractUserId(request);
		userService.changeMyPassword(userId, dto);

		return new ResponseEntity<>(ApiResponse.success(200, "Password changed successfully", null), HttpStatus.OK);
	}

	// GET PURCHASES
	@GetMapping("/purchases")
	public ResponseEntity<ApiResponse<List<PurchaseLogResponseDTO>>> getMyPurchases(HttpServletRequest request) {

		Integer userId = extractUserId(request);
		List<PurchaseLogResponseDTO> purchases = purchaseLogService.getPurchasesByUser(userId);

		return new ResponseEntity<>(ApiResponse.success(200, "Purchase history fetched successfully", purchases),
				HttpStatus.OK);
	}

	// COUNT PURCHASES
	@GetMapping("/purchases/count")
	public ResponseEntity<ApiResponse<Long>> getMyPurchaseCount(HttpServletRequest request) {

		Integer userId = extractUserId(request);
		long count = purchaseLogService.getPurchaseCount(userId);

		return new ResponseEntity<>(ApiResponse.success(200, "Purchase count fetched successfully", count),
				HttpStatus.OK);
	}

	// INVENTORY IDS
	@GetMapping("/purchases/inventory-ids")
	public ResponseEntity<ApiResponse<List<Integer>>> getMyPurchasedInventoryIds(HttpServletRequest request) {

		Integer userId = extractUserId(request);
		List<Integer> ids = purchaseLogService.getInventoryIdsByUser(userId);

		return new ResponseEntity<>(ApiResponse.success(200, "Inventory IDs fetched successfully", ids), HttpStatus.OK);
	}

	// CHECK PURCHASE
	@GetMapping("/purchases/check/{inventoryId}")
	public ResponseEntity<ApiResponse<Boolean>> checkPurchase(HttpServletRequest request,
			@PathVariable Integer inventoryId) {

		Integer userId = extractUserId(request);
		boolean hasPurchased = purchaseLogService.hasPurchased(userId, inventoryId);

		String message = hasPurchased ? "User has already purchased this item" : "User has not purchased this item";

		return new ResponseEntity<>(ApiResponse.success(200, message, hasPurchased), HttpStatus.OK);
	}

	// LOG PURCHASE
	@PostMapping("/purchases")
	public ResponseEntity<ApiResponse<PurchaseLogResponseDTO>> logPurchase(HttpServletRequest request,
			@RequestBody PurchaseLogRequestDTO dto) {

		Integer userId = extractUserId(request);
		dto.setUserId(userId);

		PurchaseLogResponseDTO purchase = purchaseLogService.addPurchase(dto);

		return new ResponseEntity<>(ApiResponse.success(201, "Purchase logged successfully", purchase),
				HttpStatus.CREATED);
	}

	// GET /api/v1/user/purchases/{userId}
	// Validates that the JWT userId matches the path userId (users can only see own
	// purchases)
	@GetMapping("/purchases/{userId}")
	public ResponseEntity<ApiResponse<List<PurchaseLogResponseDTO>>> getPurchasesByUserId(HttpServletRequest request,
			@PathVariable Integer userId) {

		Integer tokenUserId = extractUserId(request);

		// Security check: registered user can only access own purchases
		// Admin / StoreOwner can see any userId's purchases via store-owner controller
		if (!tokenUserId.equals(userId)) {
			throw new ForbiddenException("You are not allowed to view another user's purchases");
		}

		List<PurchaseLogResponseDTO> purchases = purchaseLogService.getPurchasesByUser(userId);
		return new ResponseEntity<>(ApiResponse.success(200, "Purchase history fetched successfully", purchases),
				HttpStatus.OK);
	}

	// GET /api/v1/user/dashboard
	// One-shot endpoint: profile + purchase summary in a single response
	// No need to call 3 separate APIs — great for frontend dashboard pages
	@GetMapping("/dashboard")
	public ResponseEntity<ApiResponse<UserDashboardDTO>> getMyDashboard(HttpServletRequest request) {

		Integer userId = extractUserId(request);

		// 1. Get profile
		UserResponseDTO profile = userService.getMyProfile(userId);

		// 2. Get purchase count
		long count = purchaseLogService.getPurchaseCount(userId);

		// 3. Get purchased inventory IDs
		List<Integer> inventoryIds = purchaseLogService.getInventoryIdsByUser(userId);

		// 4. Build dashboard object
		UserDashboardDTO dashboard = new UserDashboardDTO();
		dashboard.setUserId(profile.getUserId());
		dashboard.setFirstName(profile.getFirstName());
		dashboard.setLastName(profile.getLastName());
		dashboard.setUserName(profile.getUserName());
		dashboard.setPhoneNumber(profile.getPhoneNumber());
		dashboard.setRoleName(profile.getRole() != null ? profile.getRole().getPermRole() : "Guest");
		dashboard.setTotalPurchases(count);
		dashboard.setPurchasedInventoryIds(inventoryIds);

		return ResponseEntity.ok(ApiResponse.success(200, "Dashboard data fetched successfully", dashboard));
	}
}