package com.bookinventory.user.controller;

import com.bookinventory.common.exception.BadRequestException;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.user.common.config.JwtAuthFilter;
import com.bookinventory.user.dto.PurchaseLogRequestDTO;
import com.bookinventory.user.dto.PurchaseLogResponseDTO;
import com.bookinventory.user.service.PurchaseLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    value = PurchaseLogController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class
    )
)
class PurchaseLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ─────────────────────────────────────────────────────────────
    // ObjectMapper — converts Java objects TO JSON strings
    // and JSON strings BACK TO Java objects
    //
    // We use it to serialize our request DTOs into JSON
    // so we can send them in POST request bodies
    //
    // Example:
    //   new PurchaseLogRequestDTO(1, 101)
    //   → objectMapper.writeValueAsString(dto)
    //   → "{\"userId\":1,\"inventoryId\":101}"
    // ─────────────────────────────────────────────────────────────
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseLogService purchaseLogService;

    // Shared DTOs
    private PurchaseLogResponseDTO responseDTO1;
    private PurchaseLogResponseDTO responseDTO2;

    @BeforeEach
    void setUp() {
        responseDTO1 = new PurchaseLogResponseDTO(
                1, 101, "John", "Doe", "johndoe");

        responseDTO2 = new PurchaseLogResponseDTO(
                1, 202, "John", "Doe", "johndoe");
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: POST /api/v1/purchases
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /api/v1/purchases → 201 when purchase logged successfully")
    void addPurchase_WhenValid_ShouldReturn201() throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        PurchaseLogRequestDTO requestDTO =
                new PurchaseLogRequestDTO(1, 101);

        when(purchaseLogService.addPurchase(any(PurchaseLogRequestDTO.class)))
                .thenReturn(responseDTO1);

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                post("/api/v1/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                // Convert requestDTO object to JSON string for request body
                .content(objectMapper.writeValueAsString(requestDTO)))

            // 201 CREATED
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.message")
                    .value("Purchase logged successfully"))

            // Check returned purchase data
            .andExpect(jsonPath("$.data.userId").value(1))
            .andExpect(jsonPath("$.data.inventoryId").value(101))
            .andExpect(jsonPath("$.data.userFirstName").value("John"))
            .andExpect(jsonPath("$.data.userLastName").value("Doe"))
            .andExpect(jsonPath("$.data.userName").value("johndoe"));

        verify(purchaseLogService, times(1))
                .addPurchase(any(PurchaseLogRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/purchases → 400 when duplicate purchase")
    void addPurchase_WhenDuplicate_ShouldReturn400() throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        PurchaseLogRequestDTO requestDTO =
                new PurchaseLogRequestDTO(1, 101);

        // Service throws BadRequestException for duplicate
        when(purchaseLogService.addPurchase(any(PurchaseLogRequestDTO.class)))
                .thenThrow(new BadRequestException(
                        "User with ID 1 has already purchased " +
                        "inventory item with ID 101"));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                post("/api/v1/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))

            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                    .value(org.hamcrest.Matchers
                            .containsString("already purchased")));
    }

    @Test
    @DisplayName("POST /api/v1/purchases → 400 when request body missing required fields")
    void addPurchase_WhenMissingFields_ShouldReturn400() throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        // Both userId and inventoryId are null — violates @NotNull
        PurchaseLogRequestDTO invalidDTO =
                new PurchaseLogRequestDTO(null, null);

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                post("/api/v1/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))

            // @Valid catches this — returns 400 Validation failed
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));

        // Service should NEVER be called if validation fails
        verify(purchaseLogService, never())
                .addPurchase(any());
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: GET /api/v1/purchases/user/{userId}
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/v1/purchases/user/1 → 200 with purchase list")
    void getPurchasesByUser_WhenUserExists_ShouldReturn200()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.getPurchasesByUser(1))
                .thenReturn(Arrays.asList(responseDTO1, responseDTO2));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/purchases/user/1")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].inventoryId").value(101))
            .andExpect(jsonPath("$.data[1].inventoryId").value(202));

        verify(purchaseLogService, times(1)).getPurchasesByUser(1);
    }

    @Test
    @DisplayName("GET /api/v1/purchases/user/99 → 404 when user not found")
    void getPurchasesByUser_WhenUserNotFound_ShouldReturn404()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.getPurchasesByUser(99))
                .thenThrow(new ResourceNotFoundException(
                        "User", "userId", 99));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(get("/api/v1/purchases/user/99")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.status").value(404));
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: GET /api/v1/purchases/user/{userId}/inventory-ids
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/v1/purchases/user/1/inventory-ids → 200 with ID list")
    void getInventoryIdsByUser_ShouldReturn200WithIds() throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.getInventoryIdsByUser(1))
                .thenReturn(Arrays.asList(101, 202, 303));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/purchases/user/1/inventory-ids")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(3))
            .andExpect(jsonPath("$.data[0]").value(101))
            .andExpect(jsonPath("$.data[1]").value(202))
            .andExpect(jsonPath("$.data[2]").value(303));
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: GET /api/v1/purchases/user/{userId}/inventory/{id}/check
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET .../check → 200 true when user has purchased item")
    void hasPurchased_WhenPurchased_ShouldReturn200True()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.hasPurchased(1, 101))
                .thenReturn(true);

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/purchases/user/1/inventory/101/check")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(true))
            .andExpect(jsonPath("$.message")
                    .value("User has already purchased this item"));
    }

    @Test
    @DisplayName("GET .../check → 200 false when user has not purchased item")
    void hasPurchased_WhenNotPurchased_ShouldReturn200False()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.hasPurchased(1, 999))
                .thenReturn(false);

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/purchases/user/1/inventory/999/check")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value(false))
            .andExpect(jsonPath("$.message")
                    .value("User has not purchased this item"));
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: GET /api/v1/purchases/user/{userId}/count
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/v1/purchases/user/1/count → 200 with count")
    void getPurchaseCount_ShouldReturn200WithCount() throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(purchaseLogService.getPurchaseCount(1))
                .thenReturn(5L);

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/purchases/user/1/count")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(5));
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: GET /api/v1/purchases/inventory/{inventoryId}
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/v1/purchases/inventory/101 → 200 with buyers list")
    void getPurchasesByInventory_ShouldReturn200WithList()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        PurchaseLogResponseDTO buyer2 =
                new PurchaseLogResponseDTO(
                        2, 101, "Jane", "Smith", "janesmith");

        when(purchaseLogService.getPurchasesByInventory(101))
                .thenReturn(Arrays.asList(responseDTO1, buyer2));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                get("/api/v1/purchases/inventory/101")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].userName").value("johndoe"))
            .andExpect(jsonPath("$.data[1].userName").value("janesmith"));
    }
}