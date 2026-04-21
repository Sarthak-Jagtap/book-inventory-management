package com.bookinventory.user.controller;

import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.user.common.config.JwtAuthFilter;
import com.bookinventory.user.dto.PermRoleResponseDTO;
import com.bookinventory.user.repository.UserRepository;
import com.bookinventory.user.service.PermRoleService;
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
    value = PermRoleController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class
    )
)
class PermRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PermRoleService permRoleService;
    
    @MockBean
    private UserRepository userRepository;


    // Sample DTOs reused across tests
    private PermRoleResponseDTO guestDTO;
    private PermRoleResponseDTO registeredUserDTO;
    private PermRoleResponseDTO adminDTO;

    @BeforeEach
    void setUp() {
        guestDTO          = new PermRoleResponseDTO(1, "Guest");
        registeredUserDTO = new PermRoleResponseDTO(2, "RegisteredUser");
        adminDTO          = new PermRoleResponseDTO(4, "Admin");
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: GET /api/v1/roles
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/v1/roles → 200 with list of all roles")
    void getAllRoles_ShouldReturn200WithRolesList() throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(permRoleService.getAllRoles())
                .thenReturn(Arrays.asList(
                        guestDTO, registeredUserDTO, adminDTO));

        // ── ACT + ASSERT ──────────────────────────────────────────
        // mockMvc.perform() fires the HTTP request
        // .andExpect() chains checks on the response
        mockMvc.perform(
                // Build a GET request to this URL
                get("/api/v1/roles")
                // Tell the server we accept JSON back
                .contentType(MediaType.APPLICATION_JSON))

            // Check HTTP status is 200 OK
            .andExpect(status().isOk())

            // Check the response body fields
            // $ = root of JSON response
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message")
                    .value("Roles fetched successfully"))

            // Check the data array
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(3))

            // Check first item
            .andExpect(jsonPath("$.data[0].roleNumber").value(1))
            .andExpect(jsonPath("$.data[0].permRole").value("Guest"))

            // Check second item
            .andExpect(jsonPath("$.data[1].roleNumber").value(2))
            .andExpect(jsonPath("$.data[1].permRole")
                    .value("RegisteredUser"))

            // Check third item
            .andExpect(jsonPath("$.data[2].roleNumber").value(4))
            .andExpect(jsonPath("$.data[2].permRole").value("Admin"));

        // Verify service was called exactly once
        verify(permRoleService, times(1)).getAllRoles();
    }

    @Test
    @DisplayName("GET /api/v1/roles → 200 with empty list when no roles exist")
    void getAllRoles_WhenEmpty_ShouldReturn200WithEmptyList()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(permRoleService.getAllRoles())
                .thenReturn(Collections.emptyList());

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(get("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: GET /api/v1/roles/{roleNumber}
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /api/v1/roles/2 → 200 with correct role")
    void getRoleById_WhenRoleExists_ShouldReturn200() throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        when(permRoleService.getRoleById(2))
                .thenReturn(registeredUserDTO);

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                // Path variable {roleNumber} = 2
                get("/api/v1/roles/2")
                .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message")
                    .value("Role fetched successfully"))
            .andExpect(jsonPath("$.data.roleNumber").value(2))
            .andExpect(jsonPath("$.data.permRole")
                    .value("RegisteredUser"));

        verify(permRoleService, times(1)).getRoleById(2);
    }

    @Test
    @DisplayName("GET /api/v1/roles/99 → 404 when role not found")
    void getRoleById_WhenRoleNotFound_ShouldReturn404() throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        // Service throws exception — controller's exception handler
        // catches it and returns 404
        when(permRoleService.getRoleById(99))
                .thenThrow(new ResourceNotFoundException(
                        "Role", "roleNumber", 99));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(get("/api/v1/roles/99")
                .contentType(MediaType.APPLICATION_JSON))

            // GlobalExceptionHandler converts this to 404
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.statusCode").value(404))
            .andExpect(jsonPath("$.message").value(
                    org.hamcrest.Matchers.containsString("99")));

        verify(permRoleService, times(1)).getRoleById(99);
    }
}