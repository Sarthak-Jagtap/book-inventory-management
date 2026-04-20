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
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<CartOptionResponse>>> getOptions(@PathVariable String isbn) {
        List<CartOptionResponse> options = service.getCartOptionsByIsbn(isbn);

        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.OK.value(), "Cart options fetched successfully", options),
                HttpStatus.OK
        );
    }
    
    @PostMapping("/select")
    public ResponseEntity<ApiResponse<CartItemResponse>> selectQuality(@RequestParam Integer userId,
                                                                       @RequestParam String isbn,
                                                                       @RequestParam Integer rank) {
        CartItemResponse response = service.selectCartQuality(userId, isbn, rank);

        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.OK.value(), "Cart quality selected successfully", response),
                HttpStatus.OK
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartItemResponse>> add(@Valid @RequestBody AddToCartRequest request) {
        CartItemResponse response = service.addToCart(request);

        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.OK.value(), "Item added to cart successfully", response),
                HttpStatus.OK
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart(@PathVariable Integer userId) {
        List<CartItemResponse> cartItems = service.getCartByUser(userId);

        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.OK.value(), "Cart fetched successfully", cartItems),
                HttpStatus.OK
        );
    }

    @GetMapping("/view/{userId}")
    public ResponseEntity<ApiResponse<List<CartViewResponse>>> getCartView(@PathVariable Integer userId) {
        List<CartViewResponse> cartView = service.getCartViewByUser(userId);

        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.OK.value(), "Cart view fetched successfully", cartView),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<Object>> remove(@RequestParam Integer userId,
                                                      @RequestParam String isbn) {
        service.removeFromCart(userId, isbn);

        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.OK.value(), "Removed from cart"),
                HttpStatus.OK
        );
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkoutAll(@Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = service.checkoutAll(request);

        return new ResponseEntity<>(
                ApiResponse.success(HttpStatus.OK.value(), "Checkout processed successfully", response),
                HttpStatus.OK
        );
    }
}