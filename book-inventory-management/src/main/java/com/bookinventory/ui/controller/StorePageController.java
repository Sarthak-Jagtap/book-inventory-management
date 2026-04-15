package com.bookinventory.ui.controller;

import com.bookinventory.book.entity.Book;
import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.cart.dto.AddToCartRequest;
import com.bookinventory.cart.dto.CartOptionResponse;
import com.bookinventory.cart.dto.CartViewResponse;
import com.bookinventory.cart.dto.CheckoutResponse;
import com.bookinventory.cart.service.CartService;
import com.bookinventory.user.entity.User;
import com.bookinventory.user.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.bookinventory.common.exception.ResourceNotFoundException;

import java.util.List;

@Controller
@RequestMapping("/user-page")
public class StorePageController {

    private final CartService cartService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public StorePageController(CartService cartService,
                               BookRepository bookRepository,
                               UserRepository userRepository) {
        this.cartService = cartService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/cart/{userId}")
    public String showCartPage(@PathVariable Integer userId, Model model) {
        List<CartViewResponse> cartItems = cartService.getCartViewByUser(userId);
        String userName = getUserName(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("userName", userName);
        model.addAttribute("cartItems", cartItems);
        return "cart-page";
    }

    @GetMapping("/checkout/{userId}")
    public String showCheckoutPage(@PathVariable Integer userId, Model model) {
        List<CartViewResponse> cartItems = cartService.getCartViewByUser(userId);
        String userName = getUserName(userId);

        boolean canCheckout = !cartItems.isEmpty() && cartItems.stream().allMatch(CartViewResponse::isSelected);

        model.addAttribute("userId", userId);
        model.addAttribute("userName", userName);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("canCheckout", canCheckout);
        return "checkout-page";
    }

    @GetMapping("/book/{userId}/{isbn}")
    public String showBookSelectionPage(@PathVariable Integer userId,
                                        @PathVariable String isbn,
                                        @RequestParam(defaultValue = "cart") String next,
                                        Model model) {
        Book book = bookRepository.findById(isbn)
        		.orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));

        String userName = getUserName(userId);

        List<CartOptionResponse> options = cartService.getCartOptionsByIsbn(isbn);

        model.addAttribute("userId", userId);
        model.addAttribute("userName", userName);
        model.addAttribute("isbn", isbn);
        model.addAttribute("bookTitle", book.getTitle());
        model.addAttribute("options", options);
        model.addAttribute("next", next);
        model.addAttribute("hasAvailableCopies", !options.isEmpty());

        return "book-selection-page";
    }

    @PostMapping("/cart/add")
    public String addToCart(@ModelAttribute AddToCartRequest request,
                            @RequestParam(defaultValue = "cart") String next) {
        cartService.addToCart(request);

        if ("checkout".equals(next)) {
            return "redirect:/user-page/checkout/" + request.getUserId();
        }

        return "redirect:/user-page/cart/" + request.getUserId();
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Integer userId,
                                 @RequestParam String isbn) {
        cartService.removeFromCart(userId, isbn);
        return "redirect:/user-page/cart/" + userId;
    }

    @PostMapping("/cart/checkout")
    public String checkoutAll(@RequestParam Integer userId, Model model) {

        CheckoutResponse response = cartService.checkoutAll(userId);

        if (response.isSuccess()) {
            List<CartViewResponse> cartItems = cartService.getCartViewByUser(userId);
            String userName = getUserName(userId);

            model.addAttribute("userId", userId);
            model.addAttribute("userName", userName);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("checkoutResponse", response);
            model.addAttribute("canCheckout", false);

            return "checkout-page";
        }

        List<CartViewResponse> cartItems = cartService.getCartViewByUser(userId);
        String userName = getUserName(userId);
        boolean canCheckout = !cartItems.isEmpty() && cartItems.stream().allMatch(CartViewResponse::isSelected);

        model.addAttribute("userId", userId);
        model.addAttribute("userName", userName);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("canCheckout", canCheckout);
        model.addAttribute("checkoutResponse", response);

        return "checkout-page";
    }

    private String getUserName(Integer userId) {
        User user = userRepository.findById(userId)
        		.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        return user.getFirstName() + " " + user.getLastName();
    }
}