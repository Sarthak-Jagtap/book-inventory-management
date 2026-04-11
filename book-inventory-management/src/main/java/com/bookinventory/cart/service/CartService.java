package com.bookinventory.cart.service;

import com.bookinventory.cart.dto.AddToCartRequest;
import com.bookinventory.cart.dto.CartItemResponse;
import com.bookinventory.cart.dto.CartOptionResponse;
import com.bookinventory.cart.dto.CartViewResponse;
import com.bookinventory.cart.dto.CheckoutResponse;

import java.util.List;

public interface CartService {

    List<CartOptionResponse> getCartOptionsByIsbn(String isbn);

    CartItemResponse addToCart(AddToCartRequest request);

    List<CartItemResponse> getCartByUser(Integer userId);

    List<CartViewResponse> getCartViewByUser(Integer userId);

    void removeFromCart(Integer userId, String isbn);

    CheckoutResponse checkoutAll(Integer userId);
}