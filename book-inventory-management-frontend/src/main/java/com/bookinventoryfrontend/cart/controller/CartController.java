package com.bookinventoryfrontend.cart.controller;

import com.bookinventoryfrontend.cart.dto.AddToCartRequest;
import com.bookinventoryfrontend.cart.dto.CartItemResponse;
import com.bookinventoryfrontend.cart.dto.CartOptionResponse;
import com.bookinventoryfrontend.cart.dto.CartViewResponse;
import com.bookinventoryfrontend.cart.dto.CheckoutItemRequest;
import com.bookinventoryfrontend.cart.dto.CheckoutRequest;
import com.bookinventoryfrontend.cart.dto.CheckoutResponse;
import com.bookinventoryfrontend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user/cart")
public class CartController {

    private final RestClient restClient;

    public CartController(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    @GetMapping("/options")
    public String getCartOptionsByIsbn(@RequestParam String isbn, Model model) {
        if (isbn == null || isbn.isBlank()) {
            model.addAttribute("apiError", "ISBN is required.");
            model.addAttribute("options", List.of());
            return "cart/options";
        }

        try {
            ApiResponse<List<CartOptionResponse>> response = restClient.get()
                    .uri("/api/v1/user/cart/options/{isbn}", isbn)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<CartOptionResponse>>>() {});

            model.addAttribute("options", response != null ? response.getData() : List.of());
            model.addAttribute("isbn", isbn);
            return "cart/options";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            model.addAttribute("options", List.of());
            model.addAttribute("isbn", isbn);
            return "cart/options";
        }
    }

    @GetMapping("/items")
    public String getCartItemsByUser(@RequestParam Integer userId, Model model) {
        if (userId == null || userId <= 0) {
            model.addAttribute("apiError", "User ID must be greater than 0.");
            model.addAttribute("cartItems", List.of());
            return "cart/list";
        }

        try {
            ApiResponse<List<CartItemResponse>> response = restClient.get()
                    .uri("/api/v1/user/cart/{userId}", userId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<CartItemResponse>>>() {});

            model.addAttribute("cartItems", response != null ? response.getData() : List.of());
            model.addAttribute("userId", userId);
            return "cart/list";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            model.addAttribute("cartItems", List.of());
            model.addAttribute("userId", userId);
            return "cart/list";
        }
    }

    @GetMapping("/view")
    public String showCartViewPage(@RequestParam(required = false) Integer userId, Model model) {
        model.addAttribute("searchedUserId", userId);

        if (userId == null) {
            model.addAttribute("cartView", List.of());
            model.addAttribute("optionsByIsbn", Map.of());
            return "cart/view";
        }

        return "redirect:/user/cart/view/" + userId;
    }

    @GetMapping("/view/{userId}")
    public String getCartView(@PathVariable Integer userId, Model model) {
        model.addAttribute("searchedUserId", userId);

        try {
            ApiResponse<List<CartViewResponse>> response = restClient.get()
                    .uri("/api/v1/user/cart/view/{userId}", userId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<CartViewResponse>>>() {});

            List<CartViewResponse> cartView = response != null ? response.getData() : List.of();

            Map<String, List<CartOptionResponse>> optionsByIsbn = new HashMap<>();
            for (CartViewResponse item : cartView) {
                List<CartOptionResponse> options = item.getQualityOptions() != null
                        ? item.getQualityOptions()
                        : List.of();
                optionsByIsbn.put(item.getIsbn(), options);
            }

            model.addAttribute("cartView", cartView);
            model.addAttribute("optionsByIsbn", optionsByIsbn);
            model.addAttribute("userId", userId);
            return "cart/view";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("cartView", List.of());
            model.addAttribute("optionsByIsbn", Map.of());
            model.addAttribute("userId", userId);
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "cart/view";
        }
    }

    @GetMapping("/add")
    public String showAddToCartForm(Model model) {
        if (!model.containsAttribute("addToCartRequest")) {
            model.addAttribute("addToCartRequest", new AddToCartRequest());
        }
        return "cart/add";
    }

    @PostMapping("/add")
    public String addToCart(@Valid @ModelAttribute("addToCartRequest") AddToCartRequest request,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "cart/add";
        }

        try {
            restClient.post()
                    .uri("/api/v1/user/cart/add")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/user/cart/view/" + request.getUserId();
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return "cart/add";
        }
    }

    @PostMapping("/add-inline")
    public String addToCartInline(@RequestParam Integer userId,
                                  @RequestParam String isbn,
                                  RedirectAttributes redirectAttributes) {
        if (userId == null || userId <= 0) {
            redirectAttributes.addFlashAttribute("apiError", "User ID must be greater than 0.");
            return "redirect:/inventory-cart-dashboard";
        }

        if (isbn == null || isbn.isBlank()) {
            redirectAttributes.addFlashAttribute("apiError", "ISBN is required.");
            return "redirect:/inventory-cart-dashboard";
        }

        try {
            AddToCartRequest request = new AddToCartRequest();
            request.setUserId(userId);
            request.setIsbn(isbn);

            restClient.post()
                    .uri("/api/v1/user/cart/add")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            redirectAttributes.addFlashAttribute("successMessage", "Book added to cart successfully.");
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("apiError", extractErrorMessage(ex));
        }

        return "redirect:/inventory-cart-dashboard";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Integer userId,
                                 @RequestParam String isbn,
                                 Model model) {
        try {
            restClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/user/cart/remove")
                            .queryParam("userId", userId)
                            .queryParam("isbn", isbn)
                            .build())
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/user/cart/view/" + userId;
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return getCartView(userId, model);
        }
    }

    @PostMapping("/remove/run")
    public String runRemove(@RequestParam Integer userId,
                            @RequestParam String isbn,
                            RedirectAttributes redirectAttributes) {
        if (userId == null || userId <= 0) {
            redirectAttributes.addFlashAttribute("apiError", "User ID must be greater than 0.");
            return "redirect:/inventory-cart-dashboard";
        }

        if (isbn == null || isbn.isBlank()) {
            redirectAttributes.addFlashAttribute("apiError", "ISBN is required.");
            return "redirect:/inventory-cart-dashboard";
        }

        try {
            restClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/user/cart/remove")
                            .queryParam("userId", userId)
                            .queryParam("isbn", isbn)
                            .build())
                    .retrieve()
                    .toBodilessEntity();

            redirectAttributes.addFlashAttribute("successMessage", "Cart item removed successfully.");
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("apiError", extractErrorMessage(ex));
        }

        return "redirect:/inventory-cart-dashboard";
    }

    @PostMapping("/checkout")
    public String checkoutFromView(@RequestParam Integer userId,
                                   @RequestParam("isbn") List<String> isbns,
                                   @RequestParam("rank") List<Integer> ranks,
                                   Model model) {
        if (userId == null || userId <= 0) {
            model.addAttribute("apiError", "User ID must be greater than 0.");
            return getCartView(userId, model);
        }

        if (isbns == null || ranks == null || isbns.isEmpty() || ranks.isEmpty()) {
            model.addAttribute("apiError", "Cart items and rank selections are required.");
            return getCartView(userId, model);
        }

        if (isbns.size() != ranks.size()) {
            model.addAttribute("apiError", "Each cart item must have one selected rank.");
            return getCartView(userId, model);
        }

        try {
            CheckoutRequest request = new CheckoutRequest();
            request.setUserId(userId);

            List<CheckoutItemRequest> items = new ArrayList<>();
            for (int i = 0; i < isbns.size(); i++) {
                if (isbns.get(i) == null || isbns.get(i).isBlank() || ranks.get(i) == null) {
                    model.addAttribute("apiError", "Each cart item must have ISBN and rank.");
                    return getCartView(userId, model);
                }
                items.add(new CheckoutItemRequest(isbns.get(i), ranks.get(i)));
            }
            request.setItems(items);

            ApiResponse<CheckoutResponse> response = restClient.post()
                    .uri("/api/v1/user/cart/checkout")
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<CheckoutResponse>>() {});

            model.addAttribute("checkoutMessage",
                    response != null ? response.getMessage() : "Checkout completed successfully.");

            return getCartView(userId, model);
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return getCartView(userId, model);
        }
    }

    @PostMapping("/checkout/run")
    public String runCheckout(@RequestParam Integer userId,
                              RedirectAttributes redirectAttributes) {
        if (userId == null || userId <= 0) {
            redirectAttributes.addFlashAttribute("apiError", "User ID must be greater than 0.");
            return "redirect:/inventory-cart-dashboard";
        }

        redirectAttributes.addFlashAttribute("apiError",
                "Use View Cart to select rank for each item before checkout.");
        return "redirect:/user/cart/view/" + userId;
    }

    private String extractErrorMessage(HttpStatusCodeException ex) {
        String body = ex.getResponseBodyAsString();
        if (body == null || body.isBlank()) {
            return "Something went wrong.";
        }

        try {
            int messageIndex = body.indexOf("\"message\"");
            if (messageIndex == -1) {
                return body;
            }

            int colonIndex = body.indexOf(':', messageIndex);
            int firstQuote = body.indexOf('"', colonIndex + 1);
            int secondQuote = body.indexOf('"', firstQuote + 1);

            if (firstQuote != -1 && secondQuote != -1) {
                return body.substring(firstQuote + 1, secondQuote);
            }
        } catch (Exception ignored) {
        }

        return body;
    }
}