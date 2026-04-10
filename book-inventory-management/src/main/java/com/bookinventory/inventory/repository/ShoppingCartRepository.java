package com.bookinventory.inventory.repository;

import com.bookinventory.inventory.entity.ShoppingCart;
import com.bookinventory.inventory.entity.ShoppingCartId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, ShoppingCartId> {

    List<ShoppingCart> findByUserId(Integer userId);
}