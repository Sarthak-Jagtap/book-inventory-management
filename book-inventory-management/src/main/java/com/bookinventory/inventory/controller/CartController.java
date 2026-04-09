package com.bookinventory.inventory.controller;

import com.bookinventory.inventory.dto.AddToCartRequest;
import com.bookinventory.inventory.dto.CartOptionResponse;
import com.bookinventory.inventory.dto.CheckoutResponse;
import com.bookinventory.inventory.entity.ShoppingCart;
import com.bookinventory.inventory.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
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
    public ShoppingCart add(@RequestBody AddToCartRequest request) {
        return service.addToCart(request);
    }

    @GetMapping("/{userId}")
    public List<ShoppingCart> getCart(@PathVariable Integer userId) {
        return service.getCartByUser(userId);
    }

    @DeleteMapping("/remove")
    public String remove(@RequestParam Integer userId,
                         @RequestParam String isbn) {
        service.removeFromCart(userId, isbn);
        return "Removed from cart";
    }

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@RequestParam Integer userId,
                                     @RequestParam String isbn) {
        return service.checkout(userId, isbn);
    }
}