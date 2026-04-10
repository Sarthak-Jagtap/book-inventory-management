package com.bookinventory.user.controller;

import com.bookinventory.user.common.response.ApiResponse;
import com.bookinventory.user.dto.PurchaseLogRequestDTO;
import com.bookinventory.user.dto.PurchaseLogResponseDTO;
import com.bookinventory.user.service.PurchaseLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseLogController {

    private final PurchaseLogService purchaseLogService;

    // Constructor injection
    public PurchaseLogController(PurchaseLogService purchaseLogService) {
        this.purchaseLogService = purchaseLogService;
    }

    // ─────────────────────────────────────────────────────────────────
    // LOG A NEW PURCHASE
    // POST /api/purchases
    // Body: { userId, inventoryId }
    // ─────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseLogResponseDTO>> addPurchase(
            @Valid @RequestBody PurchaseLogRequestDTO requestDTO) {

        PurchaseLogResponseDTO purchase = purchaseLogService.addPurchase(requestDTO);

        return new ResponseEntity<>(
                ApiResponse.success("Purchase logged successfully", purchase),
                HttpStatus.CREATED   // 201
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // GET ALL PURCHASES BY USER
    // GET /api/purchases/user/5
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PurchaseLogResponseDTO>>> getPurchasesByUser(
            @PathVariable Integer userId) {

        List<PurchaseLogResponseDTO> purchases =
                purchaseLogService.getPurchasesByUser(userId);

        return new ResponseEntity<>(
                ApiResponse.success("Purchase history fetched successfully", purchases),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // GET ALL INVENTORY IDs PURCHASED BY A USER  (just IDs — lightweight)
    // GET /api/purchases/user/5/inventory-ids
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/user/{userId}/inventory-ids")
    public ResponseEntity<ApiResponse<List<Integer>>> getInventoryIdsByUser(
            @PathVariable Integer userId) {

        List<Integer> inventoryIds =
                purchaseLogService.getInventoryIdsByUser(userId);

        return new ResponseEntity<>(
                ApiResponse.success("Inventory IDs fetched successfully", inventoryIds),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // CHECK IF USER HAS PURCHASED A SPECIFIC INVENTORY ITEM
    // GET /api/purchases/user/5/inventory/1000003/check
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/user/{userId}/inventory/{inventoryId}/check")
    public ResponseEntity<ApiResponse<Boolean>> hasPurchased(
            @PathVariable Integer userId,
            @PathVariable Integer inventoryId) {

        boolean result = purchaseLogService.hasPurchased(userId, inventoryId);

        String message = result
                ? "User has already purchased this item"
                : "User has not purchased this item";

        return new ResponseEntity<>(
                ApiResponse.success(message, result),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // COUNT TOTAL PURCHASES BY USER
    // GET /api/purchases/user/5/count
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<Long>> getPurchaseCount(
            @PathVariable Integer userId) {

        long count = purchaseLogService.getPurchaseCount(userId);

        return new ResponseEntity<>(
                ApiResponse.success("Purchase count fetched successfully", count),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // GET ALL PURCHASES FOR A SPECIFIC INVENTORY ITEM
    // GET /api/purchases/inventory/1000003
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/inventory/{inventoryId}")
    public ResponseEntity<ApiResponse<List<PurchaseLogResponseDTO>>> getPurchasesByInventory(
            @PathVariable Integer inventoryId) {

        List<PurchaseLogResponseDTO> purchases =
                purchaseLogService.getPurchasesByInventory(inventoryId);

        return new ResponseEntity<>(
                ApiResponse.success("Purchases fetched successfully", purchases),
                HttpStatus.OK
        );
    }
}