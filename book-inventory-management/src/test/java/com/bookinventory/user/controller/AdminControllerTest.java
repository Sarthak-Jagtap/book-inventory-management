package com.bookinventory.user.controller;

import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.user.common.config.JwtAuthFilter;
import com.bookinventory.user.dto.*;
import com.bookinventory.user.service.PermRoleService;
import com.bookinventory.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    value = AdminController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class
    )
)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private PermRoleService permRoleService;

    // ── Shared test data ──────────────────────────────────────────
    private UserResponseDTO user1Response;
    private UserResponseDTO user2Response;
    private PermRoleResponseDTO registeredUserRole;
    private PermRoleResponseDTO adminRole;

    @BeforeEach
    void setUp() {

        registeredUserRole = new PermRoleResponseDTO(2, "RegisteredUser");
        adminRole          = new PermRoleResponseDTO(4, "Admin");

        user1Response = new UserResponseDTO();
        user1Response.setUserId(1);
        user1Response.setFirstName("John");
        user1Response.setLastName("Doe");
        user1Response.setUserName("johndoe");
        user1Response.setPhoneNumber("(123) 456-7890");
        user1Response.setRole(registeredUserRole);

        user2Response = new UserResponseDTO();
        user2Response.setUserId(2);
        user2Response.setFirstName("Jane");
        user2Response.setLastName("Smith");
        user2Response.setUserName("janesmith");
        user2Response.setRole(adminRole);
    }

    // ═════════════════════════════════════════════════════════════
    // GROUP 1 — User management endpoints
    // ═════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("User management tests")
    class UserManagementTests {

        @Test
        @DisplayName("GET /admin/users → 200 with all users")
        void getAllUsers_ShouldReturn200WithAllUsers()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            when(userService.getAllUsers())
                    .thenReturn(Arrays.asList(
                            user1Response, user2Response));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/admin/users")
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("All users fetched successfully"))
                .andExpect(jsonPath("$.data.length()").value(2))

                // First user
                .andExpect(jsonPath("$.data[0].userId").value(1))
                .andExpect(jsonPath("$.data[0].userName")
                        .value("johndoe"))
                .andExpect(jsonPath("$.data[0].role.permRole")
                        .value("RegisteredUser"))

                // Second user
                .andExpect(jsonPath("$.data[1].userId").value(2))
                .andExpect(jsonPath("$.data[1].userName")
                        .value("janesmith"))
                .andExpect(jsonPath("$.data[1].role.permRole")
                        .value("Admin"));

            verify(userService, times(1)).getAllUsers();
        }

        @Test
        @DisplayName("GET /admin/users → 200 with empty list when no users")
        void getAllUsers_WhenNoUsers_ShouldReturn200Empty()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            when(userService.getAllUsers())
                    .thenReturn(Collections.emptyList());

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(get("/api/v1/admin/users")
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
        }

        @Test
        @DisplayName("GET /admin/users/{userId} → 200 when user found")
        void getUserById_WhenUserExists_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            when(userService.getUserById(1))
                    .thenReturn(user1Response);

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/admin/users/1")
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.userName")
                        .value("johndoe"));

            verify(userService, times(1)).getUserById(1);
        }

        @Test
        @DisplayName("GET /admin/users/99 → 404 when user not found")
        void getUserById_WhenUserNotFound_ShouldReturn404()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            when(userService.getUserById(99))
                    .thenThrow(new ResourceNotFoundException(
                            "User", "userId", 99));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/admin/users/99")
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers
                                .containsString("99")));
        }

        @Test
        @DisplayName("PATCH /admin/users/{userId} → 200 when update successful")
        void updateUser_WhenValid_ShouldReturn200() throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            UserUpdateRequestDTO updateDTO =
                    new UserUpdateRequestDTO(
                            "NewLast", "NewFirst", null, null);

            UserResponseDTO updatedResponse = new UserResponseDTO();
            updatedResponse.setUserId(1);
            updatedResponse.setFirstName("NewFirst");
            updatedResponse.setLastName("NewLast");
            updatedResponse.setUserName("johndoe");
            updatedResponse.setRole(registeredUserRole);

            when(userService.updateUserById(
                    eq(1), any(UserUpdateRequestDTO.class)))
                    .thenReturn(updatedResponse);

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    patch("/api/v1/admin/users/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper
                            .writeValueAsString(updateDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User updated successfully"))
                .andExpect(jsonPath("$.data.firstName")
                        .value("NewFirst"))
                .andExpect(jsonPath("$.data.lastName")
                        .value("NewLast"));

            verify(userService, times(1))
                    .updateUserById(eq(1),
                            any(UserUpdateRequestDTO.class));
        }

        @Test
        @DisplayName("PUT /admin/users/{userId} → 200 when PUT update successful")
        void updateUserPut_WhenValid_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            UserUpdateRequestDTO updateDTO =
                    new UserUpdateRequestDTO(
                            null, "PutFirst", null, null);

            UserResponseDTO updatedResponse = new UserResponseDTO();
            updatedResponse.setUserId(1);
            updatedResponse.setFirstName("PutFirst");
            updatedResponse.setLastName("Doe");
            updatedResponse.setUserName("johndoe");
            updatedResponse.setRole(registeredUserRole);

            when(userService.updateUserById(
                    eq(1), any(UserUpdateRequestDTO.class)))
                    .thenReturn(updatedResponse);

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    put("/api/v1/admin/users/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper
                            .writeValueAsString(updateDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName")
                        .value("PutFirst"));
        }

        @Test
        @DisplayName("PATCH /admin/users/{userId}/role → 200 when role updated")
        void updateUserRole_WhenValid_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            RoleUpdateRequestDTO roleDTO =
                    new RoleUpdateRequestDTO(3);

            UserResponseDTO promotedUser = new UserResponseDTO();
            promotedUser.setUserId(1);
            promotedUser.setUserName("johndoe");
            promotedUser.setRole(
                    new PermRoleResponseDTO(3, "StoreOwner"));

            when(userService.updateUserRole(1, 3))
                    .thenReturn(promotedUser);

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    patch("/api/v1/admin/users/1/role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper
                            .writeValueAsString(roleDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User role updated successfully"))
                .andExpect(jsonPath("$.data.role.roleNumber")
                        .value(3))
                .andExpect(jsonPath("$.data.role.permRole")
                        .value("StoreOwner"));

            verify(userService, times(1)).updateUserRole(1, 3);
        }

        @Test
        @DisplayName("PATCH /admin/users/99/role → 404 when user not found")
        void updateUserRole_WhenUserNotFound_ShouldReturn404()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            RoleUpdateRequestDTO roleDTO =
                    new RoleUpdateRequestDTO(2);

            when(userService.updateUserRole(99, 2))
                    .thenThrow(new ResourceNotFoundException(
                            "User", "userId", 99));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    patch("/api/v1/admin/users/99/role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper
                            .writeValueAsString(roleDTO)))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        }
    }

    // ═════════════════════════════════════════════════════════════
    // GROUP 2 — Role management endpoints
    // ═════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Role management tests")
    class RoleManagementTests {

        @Test
        @DisplayName("GET /admin/roles → 200 with all roles")
        void getAllRoles_ShouldReturn200WithRoles()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            when(permRoleService.getAllRoles())
                    .thenReturn(Arrays.asList(
                            new PermRoleResponseDTO(1, "Guest"),
                            registeredUserRole,
                            new PermRoleResponseDTO(3, "StoreOwner"),
                            adminRole));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/admin/roles")
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Roles fetched successfully"))
                .andExpect(jsonPath("$.data.length()").value(4))
                .andExpect(jsonPath("$.data[0].permRole")
                        .value("Guest"))
                .andExpect(jsonPath("$.data[3].permRole")
                        .value("Admin"));

            verify(permRoleService, times(1)).getAllRoles();
        }

        @Test
        @DisplayName("GET /admin/roles/{roleNumber} → 200 when role found")
        void getRoleById_WhenRoleExists_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            when(permRoleService.getRoleById(2))
                    .thenReturn(registeredUserRole);

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/admin/roles/2")
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Role fetched successfully"))
                .andExpect(jsonPath("$.data.roleNumber").value(2))
                .andExpect(jsonPath("$.data.permRole")
                        .value("RegisteredUser"));

            verify(permRoleService, times(1)).getRoleById(2);
        }

        @Test
        @DisplayName("GET /admin/roles/99 → 404 when role not found")
        void getRoleById_WhenRoleNotFound_ShouldReturn404()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            when(permRoleService.getRoleById(99))
                    .thenThrow(new ResourceNotFoundException(
                            "Role", "roleNumber", 99));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/admin/roles/99")
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers
                                .containsString("99")));
        }
    }
}