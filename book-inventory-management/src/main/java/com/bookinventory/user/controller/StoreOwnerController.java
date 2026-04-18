package com.bookinventory.user.controller;

import com.bookinventory.user.dto.PurchaseLogResponseDTO;
import com.bookinventory.user.dto.PurchaseStatsDTO;
import com.bookinventory.user.dto.TopBuyerDTO;
import com.bookinventory.user.response.ApiResponse;
import com.bookinventory.user.service.PurchaseLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/store-owner")
public class StoreOwnerController {

	private final PurchaseLogService purchaseLogService;

	public StoreOwnerController(PurchaseLogService purchaseLogService) {
		this.purchaseLogService = purchaseLogService;
	}

	// GET ALL PURCHASES
	@GetMapping("/purchases")
	public ResponseEntity<ApiResponse<List<PurchaseLogResponseDTO>>> getAllPurchases() {

		List<PurchaseLogResponseDTO> purchases = purchaseLogService.getAllPurchases();

		return new ResponseEntity<>(ApiResponse.success(200, "All purchases fetched successfully", purchases),
				HttpStatus.OK);
	}

	// GET PURCHASES BY USER
	@GetMapping("/purchases/user/{userId}")
	public ResponseEntity<ApiResponse<List<PurchaseLogResponseDTO>>> getPurchasesByUser(@PathVariable Integer userId) {

		List<PurchaseLogResponseDTO> purchases = purchaseLogService.getPurchasesByUser(userId);

		return new ResponseEntity<>(ApiResponse.success(200, "Purchases fetched successfully", purchases),
				HttpStatus.OK);
	}

	// GET PURCHASES BY INVENTORY
	@GetMapping("/purchases/inventory/{inventoryId}")
	public ResponseEntity<ApiResponse<List<PurchaseLogResponseDTO>>> getPurchasesByInventory(
			@PathVariable Integer inventoryId) {

		List<PurchaseLogResponseDTO> purchases = purchaseLogService.getPurchasesByInventory(inventoryId);

		return new ResponseEntity<>(ApiResponse.success(200, "Purchases fetched successfully", purchases),
				HttpStatus.OK);
	}

	// GET /api/v1/store-owner/purchases/stats
	// Business intelligence endpoint — quick overview of all purchase activity
	@GetMapping("/purchases/stats")
	public ResponseEntity<ApiResponse<PurchaseStatsDTO>> getPurchaseStats() {
		PurchaseStatsDTO stats = purchaseLogService.getPurchaseStats();
		return ResponseEntity.ok(ApiResponse.success(200, "Purchase statistics fetched successfully", stats));
	}
	
	// GET /api/v1/store-owner/purchases/top-buyers?limit=5
	// Returns top N users by number of purchases
	// Default limit is 5 if not provided
	@GetMapping("/purchases/top-buyers")
	public ResponseEntity<ApiResponse<List<TopBuyerDTO>>> getTopBuyers(
	        @RequestParam(defaultValue = "5") int limit) {

	    List<TopBuyerDTO> topBuyers = purchaseLogService.getTopBuyers(limit);
	    return ResponseEntity.ok(
	        ApiResponse.success(200, "Top buyers fetched successfully", topBuyers));
	}
}