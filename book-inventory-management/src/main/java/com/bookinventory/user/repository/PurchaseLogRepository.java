package com.bookinventory.user.repository;

import com.bookinventory.user.entity.PurchaseLog;
import com.bookinventory.user.entity.PurchaseLogId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseLogRepository extends JpaRepository<PurchaseLog, PurchaseLogId> {

    // Core Queries

    // Get full purchase history for a specific user
    List<PurchaseLog> findById_UserId(Integer userId);

    // Check if a user has already purchased a specific inventory item
    boolean existsById_UserIdAndId_InventoryId(Integer userId, Integer inventoryId);

    // Get all purchases for a specific inventory item
    List<PurchaseLog> findById_InventoryId(Integer inventoryId);

    // Count how many books a user has purchased
    long countById_UserId(Integer userId);

    // Custom JPQL Queries

    // Get full purchase history for a user WITH user details in one query
    @Query("SELECT p FROM PurchaseLog p JOIN FETCH p.user WHERE p.id.userId = :userId")
    List<PurchaseLog> findPurchasesByUserId(@Param("userId") Integer userId);

    // Get all inventory IDs purchased by a specific user
    @Query("SELECT p.id.inventoryId FROM PurchaseLog p WHERE p.id.userId = :userId")
    List<Integer> findInventoryIdsByUserId(@Param("userId") Integer userId);

    // Delete all purchases made by a specific user
    void deleteById_UserId(Integer userId);
}