package com.bookinventory.inventory.controller;

import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.inventory.dto.InventoryResponse;
import com.bookinventory.inventory.dto.UpdateInventoryRequest;
import com.bookinventory.inventory.service.InventoryAdminService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryAdminService inventoryAdminService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void testUpdatePurchaseStatus_Success() throws Exception {
        UpdateInventoryRequest request = new UpdateInventoryRequest();
        request.setPurchased(true);

        InventoryResponse response = new InventoryResponse();
        response.setInventoryId(1);
        response.setIsbn("1234567890123");
        response.setRank(2);
        response.setPurchased(true);

        when(inventoryAdminService.updatePurchaseStatus(eq(1), any(UpdateInventoryRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/store-owner/inventory/purchase/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inventory purchase status updated successfully"))
                .andExpect(jsonPath("$.data.inventoryId").value(1))
                .andExpect(jsonPath("$.data.purchased").value(true));
    }

    @Test
    void testUpdatePurchaseStatus_NotFound() throws Exception {
        UpdateInventoryRequest request = new UpdateInventoryRequest();
        request.setPurchased(true);

        when(inventoryAdminService.updatePurchaseStatus(eq(1), any(UpdateInventoryRequest.class)))
                .thenThrow(new ResourceNotFoundException("Inventory", "inventoryId", 1));

        mockMvc.perform(patch("/api/v1/store-owner/inventory/purchase/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}