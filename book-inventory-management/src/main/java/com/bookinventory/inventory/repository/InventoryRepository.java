package com.bookinventory.inventory.repository;

import com.bookinventory.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    @Query(value = "SELECT * FROM inventory WHERE ISBN = ?1 AND Purchased = 0", nativeQuery = true)
    List<Inventory> getAvailableInventoryByIsbn(String isbn);

    @Query(value = "SELECT * FROM inventory WHERE ISBN = ?1 AND Ranks = ?2 AND Purchased = 0", nativeQuery = true)
    List<Inventory> getAvailableInventoryByIsbnAndRank(String isbn, Integer rank);

    @Query(value = "SELECT * FROM inventory WHERE ISBN = ?1 AND Ranks = ?2 AND Purchased = 0 LIMIT 1", nativeQuery = true)
    Optional<Inventory> getFirstAvailableInventoryByIsbnAndRank(String isbn, Integer rank);
}