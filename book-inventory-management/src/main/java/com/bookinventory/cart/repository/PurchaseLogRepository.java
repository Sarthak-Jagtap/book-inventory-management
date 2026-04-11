package com.bookinventory.cart.repository;

import com.bookinventory.cart.entity.PurchaseLog;
import com.bookinventory.cart.entity.PurchaseLogId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseLogRepository 
        extends JpaRepository<PurchaseLog, PurchaseLogId> {
}