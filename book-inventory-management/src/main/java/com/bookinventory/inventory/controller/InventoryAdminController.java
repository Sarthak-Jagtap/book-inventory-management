package com.bookinventory.inventory.controller;

import com.bookinventory.inventory.dto.InventoryRequest;
import com.bookinventory.inventory.dto.InventoryResponse;
import com.bookinventory.inventory.dto.InventorySummaryResponse;
import com.bookinventory.inventory.dto.UpdateInventoryRequest;
import com.bookinventory.inventory.service.InventoryAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
public class InventoryAdminController {

    private final InventoryAdminService inventoryAdminService;

    public InventoryAdminController(InventoryAdminService inventoryAdminService) {
        this.inventoryAdminService = inventoryAdminService;
    }

    @PostMapping
    public InventoryResponse addInventory(@RequestBody InventoryRequest request) {
        return inventoryAdminService.addInventory(request);
    }

    @GetMapping
    public List<InventoryResponse> getAllInventory() {
        return inventoryAdminService.getAllInventory();
    }

    @GetMapping("/{inventoryId}")
    public InventoryResponse getInventoryById(@PathVariable Integer inventoryId) {
        return inventoryAdminService.getInventoryById(inventoryId);
    }

    @GetMapping("/book/{isbn}")
    public List<InventoryResponse> getInventoryByIsbn(@PathVariable String isbn) {
        return inventoryAdminService.getInventoryByIsbn(isbn);
    }

    @GetMapping("/available/{isbn}")
    public List<InventoryResponse> getAvailableInventoryByIsbn(@PathVariable String isbn) {
        return inventoryAdminService.getAvailableInventoryByIsbn(isbn);
    }

    @GetMapping("/summary/{isbn}")
    public List<InventorySummaryResponse> getInventorySummaryByIsbn(@PathVariable String isbn) {
        return inventoryAdminService.getInventorySummaryByIsbn(isbn);
    }

    @PutMapping("/{inventoryId}")
    public InventoryResponse updateInventory(@PathVariable Integer inventoryId,
                                             @RequestBody UpdateInventoryRequest request) {
        return inventoryAdminService.updateInventory(inventoryId, request);
    }

    @PutMapping("/purchase/{inventoryId}")
    public InventoryResponse markAsPurchased(@PathVariable Integer inventoryId) {
        return inventoryAdminService.markAsPurchased(inventoryId);
    }

    @DeleteMapping("/{inventoryId}")
    public String deleteInventory(@PathVariable Integer inventoryId) {
        inventoryAdminService.deleteInventory(inventoryId);
        return "Inventory item deleted successfully";
    }
}