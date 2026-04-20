package com.bookinventoryfrontend.cart.controller;

import com.bookinventoryfrontend.cart.dto.*;
import com.bookinventoryfrontend.common.dto.ApiResponse;
import jakarta.servlet.http.HttpSession;
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

    public CartController(RestClient restClient) {
        this.restClient = restClient;
    }

    private Integer getLoggedUserId(HttpSession session) {
        Object userId = session.getAttribute("USER_ID");
        return (userId instanceof Integer) ? (Integer) userId : null;
    }

    private String getLoggedRole(HttpSession session) {
        Object role = session.getAttribute("ROLE_NAME");
        return (role instanceof String) ? (String) role : "";
    }

    private boolean isUserAccessOnly(HttpSession session) {
        String role = getLoggedRole(session);
        return "RegisteredUser".equalsIgnoreCase(role);
    }

    @GetMapping("/options")
    public String getCartOptionsByIsbn(@RequestParam String isbn, HttpSession session, Model model) {
        if (!isUserAccessOnly(session)) {
            model.addAttribute("apiError", "Shop owners cannot access cart operations.");
            return "cart/options";
        }

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

    @GetMapping("/view")
    public String showCartViewPage(HttpSession session, Model model) {
        Integer userId = getLoggedUserId(session);
        if (userId == null) {
            return "redirect:/home";
        }
        return "redirect:/user/cart/view/" + userId;
    }

    @GetMapping("/view/{userId}")
    public String getCartView(@PathVariable Integer userId, HttpSession session, Model model) {
        Integer loggedId = getLoggedUserId(session);
        if (loggedId == null || !loggedId.equals(userId)) {
            model.addAttribute("apiError", "You can only view your own cart.");
            return "cart/view";
        }

        if (!isUserAccessOnly(session)) {
            model.addAttribute("apiError", "Shop owners cannot view carts.");
            return "cart/view";
        }

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
    public String showAddToCartForm(HttpSession session, Model model) {
        if (!isUserAccessOnly(session)) {
            model.addAttribute("apiError", "Shop owners cannot perform cart operations.");
        }
        if (!model.containsAttribute("addToCartRequest")) {
            model.addAttribute("addToCartRequest", new AddToCartRequest());
        }
        return "cart/add";
    }

    @PostMapping("/add")
    public String addToCart(@Valid @ModelAttribute("addToCartRequest") AddToCartRequest request,
                            BindingResult bindingResult,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Integer loggedId = getLoggedUserId(session);
        if (loggedId == null) return "redirect:/home";
        request.setUserId(loggedId);

        if (!isUserAccessOnly(session)) {
            redirectAttributes.addFlashAttribute("apiError", "Shop owners cannot add to cart.");
            return "redirect:/team/inventory-module";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("apiError", "ISBN format is incorrect. Use x-xxx-xxxxx-x");
            return "redirect:/team/inventory-module";
        }

        try {
            restClient.post()
                    .uri("/api/v1/user/cart/add")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            redirectAttributes.addFlashAttribute("successMessage", "Item added to cart successfully!");
            return "redirect:/user/cart/view/" + loggedId;
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("apiError", extractErrorMessage(ex));
            return "redirect:/team/inventory-module";
        }
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Integer userId,
                                 @RequestParam String isbn,
                                 HttpSession session,
                                 Model model) {
        Integer loggedId = getLoggedUserId(session);
        if (loggedId == null || !loggedId.equals(userId)) {
            model.addAttribute("apiError", "Access denied.");
            return "cart/view";
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

            return "redirect:/user/cart/view/" + userId;
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return getCartView(userId, session, model);
        }
    }

    @PostMapping("/checkout")
    public String checkoutFromView(@RequestParam Integer userId,
                                   @RequestParam("isbn") List<String> isbns,
                                   @RequestParam("rank") List<Integer> ranks,
                                   HttpSession session,
                                   Model model) {
        Integer loggedId = getLoggedUserId(session);
        if (loggedId == null || !loggedId.equals(userId)) {
            model.addAttribute("apiError", "Access denied.");
            return "cart/view";
        }

        if (isbns == null || ranks == null || isbns.isEmpty() || ranks.isEmpty()) {
            model.addAttribute("apiError", "Cart items and rank selections are required.");
            return getCartView(userId, session, model);
        }

        try {
            CheckoutRequest request = new CheckoutRequest();
            request.setUserId(userId);

            List<CheckoutItemRequest> items = new ArrayList<>();
            for (int i = 0; i < isbns.size(); i++) {
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

            return getCartView(userId, session, model);
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("apiError", extractErrorMessage(ex));
            return getCartView(userId, session, model);
        }
    }

    @GetMapping("/checkout")
    public String initiateCheckout(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = getLoggedUserId(session);
        if (userId == null) return "redirect:/home";

        try {
            ApiResponse<List<CartViewResponse>> response = restClient.get()
                    .uri("/api/v1/user/cart/view/{userId}", userId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<CartViewResponse>>>() {});

            List<CartViewResponse> cartView = response != null ? response.getData() : List.of();
            if (cartView.isEmpty()) {
                redirectAttributes.addFlashAttribute("apiError", "No items in cart to checkout.");
                return "redirect:/team/inventory-module";
            }

            return "redirect:/user/cart/view/" + userId;
        } catch (HttpStatusCodeException ex) {
            redirectAttributes.addFlashAttribute("apiError", extractErrorMessage(ex));
            return "redirect:/team/inventory-module";
        }
    }

    private String extractErrorMessage(HttpStatusCodeException ex) {
        String body = ex.getResponseBodyAsString();
        if (body == null || body.isBlank()) return "Something went wrong.";
        try {
            int messageIndex = body.indexOf("\"message\"");
            if (messageIndex == -1) return body;
            int colonIndex = body.indexOf(':', messageIndex);
            int firstQuote = body.indexOf('"', colonIndex + 1);
            int secondQuote = body.indexOf('"', firstQuote + 1);
            if (firstQuote != -1 && secondQuote != -1) return body.substring(firstQuote + 1, secondQuote);
        } catch (Exception ignored) {}
        return body;
    }
}