package com.bookinventory.user.service;

import com.bookinventory.user.dto.PurchaseLogRequestDTO;
import com.bookinventory.user.dto.PurchaseLogResponseDTO;

import java.util.List;

public interface PurchaseLogService {

    // Log a new purchase
    PurchaseLogResponseDTO addPurchase(PurchaseLogRequestDTO requestDTO);

    // Get full purchase history of a user
    List<PurchaseLogResponseDTO> getPurchasesByUser(Integer userId);

    // Get all inventory IDs purchased by a user 
    List<Integer> getInventoryIdsByUser(Integer userId);

    // Check if a user already purchased a specific inventory item
    boolean hasPurchased(Integer userId, Integer inventoryId);

    // Count how many items a user has purchased
    long getPurchaseCount(Integer userId);

    // Get all purchases for a specific inventory item
    List<PurchaseLogResponseDTO> getPurchasesByInventory(Integer inventoryId);
    
    // In PurchaseLogService interface — add:
    List<PurchaseLogResponseDTO> getAllPurchases();
}