package com.bookinventory.cart.service;

import com.bookinventory.cart.dto.AddToCartRequest;
import com.bookinventory.cart.dto.CartOptionResponse;
import com.bookinventory.cart.dto.CheckoutResponse;
import com.bookinventory.inventory.entity.ShoppingCart;

import java.util.List;

public interface CartService {

    List<CartOptionResponse> getCartOptionsByIsbn(String isbn);

    ShoppingCart addToCart(AddToCartRequest request);

    List<ShoppingCart> getCartByUser(Integer userId);

    void removeFromCart(Integer userId, String isbn);

    CheckoutResponse checkout(Integer userId, String isbn);
}