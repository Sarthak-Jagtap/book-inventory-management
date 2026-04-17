package com.bookinventory.inventory.controller;

import com.bookinventory.inventory.dto.InventoryRequest;
import com.bookinventory.inventory.dto.InventoryResponse;
import com.bookinventory.inventory.dto.InventorySummaryResponse;
import com.bookinventory.inventory.dto.UpdateInventoryRequest;
import com.bookinventory.inventory.service.InventoryAdminService;
import com.bookinventory.user.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/store-owner/inventory")
public class InventoryAdminController {

    private final InventoryAdminService inventoryAdminService;

    public InventoryAdminController(InventoryAdminService inventoryAdminService) {
        this.inventoryAdminService = inventoryAdminService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> addInventory(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryAdminService.addInventory(request);

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.CREATED.value(), "Inventory item added successfully", response),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAllInventory() {
        List<InventoryResponse> inventoryList = inventoryAdminService.getAllInventory();

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.OK.value(), "Inventory fetched successfully", inventoryList),
                HttpStatus.OK
        );
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventoryById(@PathVariable Integer inventoryId) {
        InventoryResponse response = inventoryAdminService.getInventoryById(inventoryId);

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.OK.value(), "Inventory item fetched successfully", response),
                HttpStatus.OK
        );
    }

    @GetMapping("/book/{isbn}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoryByIsbn(@PathVariable String isbn) {
        List<InventoryResponse> response = inventoryAdminService.getInventoryByIsbn(isbn);

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.OK.value(), "Inventory by ISBN fetched successfully", response),
                HttpStatus.OK
        );
    }

    @GetMapping("/available/{isbn}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAvailableInventoryByIsbn(@PathVariable String isbn) {
        List<InventoryResponse> response = inventoryAdminService.getAvailableInventoryByIsbn(isbn);

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.OK.value(), "Available inventory fetched successfully", response),
                HttpStatus.OK
        );
    }

    @GetMapping("/summary/{isbn}")
    public ResponseEntity<ApiResponse<List<InventorySummaryResponse>>> getInventorySummaryByIsbn(@PathVariable String isbn) {
        List<InventorySummaryResponse> response = inventoryAdminService.getInventorySummaryByIsbn(isbn);

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.OK.value(), "Inventory summary fetched successfully", response),
                HttpStatus.OK
        );
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateInventory(@PathVariable Integer inventoryId,
                                                                          @Valid @RequestBody UpdateInventoryRequest request) {
        InventoryResponse response = inventoryAdminService.updateInventory(inventoryId, request);

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.OK.value(), "Inventory item updated successfully", response),
                HttpStatus.OK
        );
    }

    @PatchMapping("/purchase/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> updatePurchaseStatus(
            @PathVariable Integer inventoryId,
            @RequestBody UpdateInventoryRequest request) {

        InventoryResponse response = inventoryAdminService.updatePurchaseStatus(inventoryId, request);

        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.OK.value(), "Inventory purchase status updated successfully", response),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<Object>> deleteInventory(@PathVariable Integer inventoryId) {
        inventoryAdminService.deleteInventory(inventoryId);

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.OK.value(), "Inventory item deleted successfully"),
                HttpStatus.OK
        );
    }

    @GetMapping("/rank/{rank}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoryByRank(@PathVariable Integer rank) {
        List<InventoryResponse> response = inventoryAdminService.getInventoryByRank(rank);

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.OK.value(), "Inventory by rank fetched successfully", response),
                HttpStatus.OK
        );
    }
}