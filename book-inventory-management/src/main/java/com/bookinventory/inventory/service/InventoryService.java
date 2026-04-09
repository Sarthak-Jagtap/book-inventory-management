package com.bookinventory.inventory.service;

import com.bookinventory.inventory.entity.Inventory;

import java.util.List;

public interface InventoryService {

    List<Inventory> getAvailableBooks(String isbn);

    Inventory addInventory(Inventory inventory);

    void markAsPurchased(Integer inventoryId);
}