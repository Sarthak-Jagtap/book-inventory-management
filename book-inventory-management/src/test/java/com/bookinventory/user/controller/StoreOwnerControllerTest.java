package com.bookinventory.user.controller;

import com.bookinventory.user.common.config.JwtAuthFilter;
import com.bookinventory.user.dto.PurchaseLogResponseDTO;
import com.bookinventory.user.service.PurchaseLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    value = StoreOwnerController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class
    )
)
class StoreOwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseLogService purchaseLogService;

    private PurchaseLogResponseDTO purchase1;
    private PurchaseLogResponseDTO purchase2;
    private PurchaseLogResponseDTO purchase3;

    @BeforeEach
    void setUp() {
        // Two purchases by user 1
        purchase1 = new PurchaseLogResponseDTO(
                1, 101, "John", "Doe", "johndoe");
        purchase2 = new PurchaseLogResponseDTO(
                1, 202, "John", "Doe", "johndoe");

        // One purchase by user 2
        purchase3 = new PurchaseLogResponseDTO(
                2, 101, "Jane", "Smith", "janesmith");
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: GET /api/v1/store-owner/purchases
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/v1/store-owner/purchases → 200 with all purchases")
    void getAllPurchases_ShouldReturn200WithAllPurchases()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.getAllPurchases())
                .thenReturn(Arrays.asList(
                        purchase1, purchase2, purchase3));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/store-owner/purchases")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message")
                    .value("All purchases fetched successfully"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(3))
            .andExpect(jsonPath("$.data[0].userId").value(1))
            .andExpect(jsonPath("$.data[0].inventoryId").value(101))
            .andExpect(jsonPath("$.data[2].userId").value(2))
            .andExpect(jsonPath("$.data[2].userName")
                    .value("janesmith"));

        verify(purchaseLogService, times(1)).getAllPurchases();
    }

    @Test
    @DisplayName("GET /api/v1/store-owner/purchases → 200 with empty list")
    void getAllPurchases_WhenNoPurchases_ShouldReturn200EmptyList()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.getAllPurchases())
                .thenReturn(Collections.emptyList());

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/store-owner/purchases")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: GET /api/v1/store-owner/purchases/user/{userId}
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/v1/store-owner/purchases/user/1 → 200 with user purchases")
    void getPurchasesByUser_WhenUserHasPurchases_ShouldReturn200()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.getPurchasesByUser(1))
                .thenReturn(Arrays.asList(purchase1, purchase2));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/store-owner/purchases/user/1")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].inventoryId").value(101))
            .andExpect(jsonPath("$.data[1].inventoryId").value(202));

        verify(purchaseLogService, times(1)).getPurchasesByUser(1);
    }

    @Test
    @DisplayName("GET /api/v1/store-owner/purchases/user/1 → 200 empty when no purchases")
    void getPurchasesByUser_WhenNoPurchases_ShouldReturn200Empty()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.getPurchasesByUser(1))
                .thenReturn(Collections.emptyList());

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/store-owner/purchases/user/1")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: GET /api/v1/store-owner/purchases/inventory/{id}
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET .../inventory/101 → 200 with all buyers of that item")
    void getPurchasesByInventory_WhenBuyersExist_ShouldReturn200()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        // Both user1 and user2 bought item 101
        when(purchaseLogService.getPurchasesByInventory(101))
                .thenReturn(Arrays.asList(purchase1, purchase3));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/store-owner/purchases/inventory/101")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(2))

            // Both buyers visible to store owner
            .andExpect(jsonPath("$.data[0].userName").value("johndoe"))
            .andExpect(jsonPath("$.data[1].userName").value("janesmith"));

        verify(purchaseLogService, times(1))
                .getPurchasesByInventory(101);
    }

    @Test
    @DisplayName("GET .../inventory/999 → 200 empty when nobody bought that item")
    void getPurchasesByInventory_WhenNoBuyers_ShouldReturn200Empty()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.getPurchasesByInventory(999))
                .thenReturn(Collections.emptyList());

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/store-owner/purchases/inventory/999")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }
}