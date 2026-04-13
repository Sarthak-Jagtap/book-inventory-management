package com.bookinventory.cart.controller;

import com.bookinventory.cart.dto.AddToCartRequest;
import com.bookinventory.cart.dto.CartItemResponse;
import com.bookinventory.cart.dto.CartOptionResponse;
import com.bookinventory.cart.dto.CartViewResponse;
import com.bookinventory.cart.dto.CheckoutResponse;
import com.bookinventory.cart.service.CartService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/options/{isbn}")
    public List<CartOptionResponse> getOptions(@PathVariable String isbn) {
        return service.getCartOptionsByIsbn(isbn);
    }

    @PostMapping("/add")
    public CartItemResponse add(@Valid @RequestBody AddToCartRequest request) {
        return service.addToCart(request);
    }

    @GetMapping("/{userId}")
    public List<CartItemResponse> getCart(@PathVariable Integer userId) {
        return service.getCartByUser(userId);
    }

    @GetMapping("/view/{userId}")
    public List<CartViewResponse> getCartView(@PathVariable Integer userId) {
        return service.getCartViewByUser(userId);
    }

    @DeleteMapping("/remove")
    public String remove(@RequestParam Integer userId,
                         @RequestParam String isbn) {
        service.removeFromCart(userId, isbn);
        return "Removed from cart";
    }

    @PostMapping("/checkout")
    public CheckoutResponse checkoutAll(@RequestParam Integer userId) {
        return service.checkoutAll(userId);
    }
}