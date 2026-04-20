package com.bookinventory.user.controller;

import com.bookinventory.user.dto.PurchaseLogRequestDTO;
import com.bookinventory.user.dto.PurchaseLogResponseDTO;
import com.bookinventory.user.common.response.ApiResponse;
import com.bookinventory.user.service.PurchaseLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseLogController {

	private final PurchaseLogService purchaseLogService;

	public PurchaseLogController(PurchaseLogService purchaseLogService) {
		this.purchaseLogService = purchaseLogService;
	}

	// ADD PURCHASE
	@PostMapping
	public ResponseEntity<ApiResponse<PurchaseLogResponseDTO>> addPurchase(
			@Valid @RequestBody PurchaseLogRequestDTO requestDTO) {

		PurchaseLogResponseDTO purchase = purchaseLogService.addPurchase(requestDTO);

		return new ResponseEntity<>(ApiResponse.success(201, "Purchase logged successfully", purchase),
				HttpStatus.CREATED);
	}

	// GET PURCHASES BY USER
	@GetMapping("/user/{userId}")
	public ResponseEntity<ApiResponse<List<PurchaseLogResponseDTO>>> getPurchasesByUser(@PathVariable Integer userId) {

		List<PurchaseLogResponseDTO> purchases = purchaseLogService.getPurchasesByUser(userId);

		return new ResponseEntity<>(ApiResponse.success(200, "Purchase history fetched successfully", purchases),
				HttpStatus.OK);
	}

	// GET INVENTORY IDS
	@GetMapping("/user/{userId}/inventory-ids")
	public ResponseEntity<ApiResponse<List<Integer>>> getInventoryIdsByUser(@PathVariable Integer userId) {

		List<Integer> inventoryIds = purchaseLogService.getInventoryIdsByUser(userId);

		return new ResponseEntity<>(ApiResponse.success(200, "Inventory IDs fetched successfully", inventoryIds),
				HttpStatus.OK);
	}

	// CHECK PURCHASE
	@GetMapping("/user/{userId}/inventory/{inventoryId}/check")
	public ResponseEntity<ApiResponse<Boolean>> hasPurchased(@PathVariable Integer userId,
			@PathVariable Integer inventoryId) {

		boolean result = purchaseLogService.hasPurchased(userId, inventoryId);

		String message = result ? "User has already purchased this item" : "User has not purchased this item";

		return new ResponseEntity<>(ApiResponse.success(200, message, result), HttpStatus.OK);
	}

	// COUNT PURCHASES
	@GetMapping("/user/{userId}/count")
	public ResponseEntity<ApiResponse<Long>> getPurchaseCount(@PathVariable Integer userId) {

		long count = purchaseLogService.getPurchaseCount(userId);

		return new ResponseEntity<>(ApiResponse.success(200, "Purchase count fetched successfully", count),
				HttpStatus.OK);
	}

	// GET PURCHASES BY INVENTORY
	@GetMapping("/inventory/{inventoryId}")
	public ResponseEntity<ApiResponse<List<PurchaseLogResponseDTO>>> getPurchasesByInventory(
			@PathVariable Integer inventoryId) {

		List<PurchaseLogResponseDTO> purchases = purchaseLogService.getPurchasesByInventory(inventoryId);

		return new ResponseEntity<>(ApiResponse.success(200, "Purchases fetched successfully", purchases),
				HttpStatus.OK);
	}
}