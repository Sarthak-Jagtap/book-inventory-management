package com.bookinventory.cart.controller;

import com.bookinventory.cart.dto.AddToCartRequest;
import com.bookinventory.cart.dto.CartItemResponse;
import com.bookinventory.cart.dto.CartOptionResponse;
import com.bookinventory.cart.dto.CartViewResponse;
import com.bookinventory.cart.dto.CheckoutRequest;
import com.bookinventory.cart.dto.CheckoutResponse;
import com.bookinventory.cart.service.CartService;
import com.bookinventory.user.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/options/{isbn}")
    public ApiResponse<List<CartOptionResponse>> getOptions(@PathVariable String isbn) {
        return ApiResponse.success(200, "Cart options fetched successfully", service.getCartOptionsByIsbn(isbn));
    }

    @GetMapping("/{userId}")
    public ApiResponse<List<CartItemResponse>> getCart(@PathVariable Integer userId) {
        return ApiResponse.success(200, "Cart items fetched successfully", service.getCartByUser(userId));
    }

    @GetMapping("/view/{userId}")
    public ApiResponse<List<CartViewResponse>> getCartView(@PathVariable Integer userId) {
        return ApiResponse.success(200, "Cart view fetched successfully", service.getCartViewByUser(userId));
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CartItemResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.success(201, "Book added to cart successfully", service.addToCart(request));
    }

    @DeleteMapping("/remove")
    public ApiResponse<Void> removeFromCart(@RequestParam Integer userId,
                                            @RequestParam String isbn) {
        service.removeFromCart(userId, isbn);
        return ApiResponse.success(200, "Cart item removed successfully");
    }

    @PostMapping("/checkout")
    public ApiResponse<CheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = service.checkoutAll(request);
        return ApiResponse.success(200, response.getMessage(), response);
    }
}