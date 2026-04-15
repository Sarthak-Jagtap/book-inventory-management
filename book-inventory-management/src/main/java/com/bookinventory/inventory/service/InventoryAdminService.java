package com.bookinventory.inventory.service;

import com.bookinventory.inventory.dto.InventoryRequest;
import com.bookinventory.inventory.dto.InventoryResponse;
import com.bookinventory.inventory.dto.InventorySummaryResponse;
import com.bookinventory.inventory.dto.UpdateInventoryRequest;

import java.util.List;

public interface InventoryAdminService {
	List<InventoryResponse> getInventoryByRank(Integer rank);

    InventoryResponse addInventory(InventoryRequest request);

    List<InventoryResponse> getAllInventory();

    InventoryResponse getInventoryById(Integer inventoryId);

    List<InventoryResponse> getInventoryByIsbn(String isbn);

    List<InventoryResponse> getAvailableInventoryByIsbn(String isbn);

    List<InventorySummaryResponse> getInventorySummaryByIsbn(String isbn);

    InventoryResponse updateInventory(Integer inventoryId, UpdateInventoryRequest request);

    InventoryResponse markAsPurchased(Integer inventoryId);

    void deleteInventory(Integer inventoryId);
}