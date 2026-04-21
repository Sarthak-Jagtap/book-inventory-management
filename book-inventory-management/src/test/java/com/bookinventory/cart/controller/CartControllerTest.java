package com.bookinventory.cart.controller;

import com.bookinventory.cart.dto.AddToCartRequest;
import com.bookinventory.cart.dto.CartItemResponse;
import com.bookinventory.cart.dto.CartOptionResponse;
import com.bookinventory.cart.dto.CartViewResponse;
import com.bookinventory.cart.dto.CheckoutItemRequest;
import com.bookinventory.cart.dto.CheckoutRequest;
import com.bookinventory.cart.dto.CheckoutResponse;
import com.bookinventory.cart.service.CartService;
import com.bookinventory.user.common.config.JwtAuthFilter;
import com.bookinventory.user.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService service;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void testGetOptions() throws Exception {
        CartOptionResponse option = new CartOptionResponse();
        option.setIsbn("1-533-73363-8");
        option.setRank(1);
        option.setCondition("Good");
        option.setPrice(BigDecimal.valueOf(499));
        option.setAvailableCount(2L);

        when(service.getCartOptionsByIsbn("1-533-73363-8")).thenReturn(List.of(option));

        mockMvc.perform(get("/api/v1/user/cart/options/1-533-73363-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Cart options fetched successfully"))
                .andExpect(jsonPath("$.data[0].isbn").value("1-533-73363-8"))
                .andExpect(jsonPath("$.data[0].rank").value(1))
                .andExpect(jsonPath("$.data[0].condition").value("Good"))
                .andExpect(jsonPath("$.data[0].price").value(499))
                .andExpect(jsonPath("$.data[0].availableCount").value(2));
    }

    @Test
    void testAddToCart() throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setUserId(1);
        request.setIsbn("1-533-73363-8");

        CartItemResponse response = new CartItemResponse(1, "1-533-73363-8");

        when(service.addToCart(any(AddToCartRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/user/cart/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Book added to cart successfully"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.isbn").value("1-533-73363-8"));
    }

    @Test
    void testGetCart() throws Exception {
        CartItemResponse item = new CartItemResponse(1, "1-533-73363-8");

        when(service.getCartByUser(1)).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/user/cart/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Cart items fetched successfully"))
                .andExpect(jsonPath("$.data[0].userId").value(1))
                .andExpect(jsonPath("$.data[0].isbn").value("1-533-73363-8"));
    }

    @Test
    void testGetCartView() throws Exception {
        CartOptionResponse option = new CartOptionResponse();
        option.setIsbn("1-533-73363-8");
        option.setRank(1);
        option.setCondition("Good");
        option.setPrice(BigDecimal.valueOf(499));
        option.setAvailableCount(2L);

        CartViewResponse response = new CartViewResponse();
        response.setUserId(1);
        response.setUserName("A B");
        response.setIsbn("1-533-73363-8");
        response.setBookTitle("Sample Book");
        response.setQualityOptions(List.of(option));

        when(service.getCartViewByUser(1)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/user/cart/view/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Cart view fetched successfully"))
                .andExpect(jsonPath("$.data[0].userId").value(1))
                .andExpect(jsonPath("$.data[0].isbn").value("1-533-73363-8"))
                .andExpect(jsonPath("$.data[0].bookTitle").value("Sample Book"))
                .andExpect(jsonPath("$.data[0].qualityOptions[0].rank").value(1))
                .andExpect(jsonPath("$.data[0].qualityOptions[0].condition").value("Good"))
                .andExpect(jsonPath("$.data[0].qualityOptions[0].price").value(499));
    }

    @Test
    void testRemoveFromCart() throws Exception {
        doNothing().when(service).removeFromCart(1, "1-533-73363-8");

        mockMvc.perform(delete("/api/v1/user/cart/remove")
                        .with(csrf())
                        .param("userId", "1")
                        .param("isbn", "1-533-73363-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Cart item removed successfully"));
    }

    @Test
    void testCheckoutAll_Success() throws Exception {
        CheckoutRequest request = new CheckoutRequest(
                1,
                List.of(new CheckoutItemRequest("1-533-73363-8", 1))
        );

        CheckoutResponse response = new CheckoutResponse(true, "Checkout successful for all cart items.");

        when(service.checkoutAll(any(CheckoutRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/user/cart/checkout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Checkout successful for all cart items."))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.message").value("Checkout successful for all cart items."));
    }

    @Test
    void testCheckoutAll_MissingSelections() throws Exception {
        CheckoutRequest request = new CheckoutRequest(1, List.of());

        CheckoutResponse response = new CheckoutResponse(false, "Select quality choice for cart items");

        when(service.checkoutAll(any(CheckoutRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/user/cart/checkout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Select quality choice for cart items"))
                .andExpect(jsonPath("$.data.success").value(false))
                .andExpect(jsonPath("$.data.message").value("Select quality choice for cart items"));
    }

    @Test
    void testCheckoutAll_EmptyCart() throws Exception {
        CheckoutRequest request = new CheckoutRequest(1, List.of());

        CheckoutResponse response = new CheckoutResponse(false, "Cart is empty");

        when(service.checkoutAll(any(CheckoutRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/user/cart/checkout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Cart is empty"))
                .andExpect(jsonPath("$.data.success").value(false))
                .andExpect(jsonPath("$.data.message").value("Cart is empty"));
    }
}