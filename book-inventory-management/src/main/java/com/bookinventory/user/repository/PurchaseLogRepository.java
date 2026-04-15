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

	List<PurchaseLog> findById_UserId(Integer userId);

	boolean existsById_UserIdAndId_InventoryId(Integer userId, Integer inventoryId);

	List<PurchaseLog> findById_InventoryId(Integer inventoryId);

	long countById_UserId(Integer userId);

	@Query("SELECT p FROM PurchaseLog p JOIN FETCH p.user WHERE p.id.userId = :userId")
	List<PurchaseLog> findPurchasesByUserId(@Param("userId") Integer userId);

	@Query("SELECT p.id.inventoryId FROM PurchaseLog p WHERE p.id.userId = :userId")
	List<Integer> findInventoryIdsByUserId(@Param("userId") Integer userId);

	void deleteById_UserId(Integer userId);
}