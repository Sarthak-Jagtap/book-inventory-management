package com.bookinventory.user.controller;

import com.bookinventory.common.exception.BadRequestException;
import com.bookinventory.common.exception.ForbiddenException;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.user.common.config.JwtAuthFilter;
import com.bookinventory.user.dto.*;
import com.bookinventory.user.service.PurchaseLogService;
import com.bookinventory.user.service.UserService;
import com.bookinventory.user.util.JwtUtil;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    value = UserController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class
    )
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private PurchaseLogService purchaseLogService;

    // ─────────────────────────────────────────────────────────────
    // JwtUtil must be @MockBean here because UserController
    // injects it directly and calls extractUserId() on every request
    //
    // We mock it so that when our fake token arrives,
    // extractUserId() returns the userId WE choose
    // ─────────────────────────────────────────────────────────────
    @MockBean
    private JwtUtil jwtUtil;

    // ─────────────────────────────────────────────────────────────
    // FAKE TOKEN STRATEGY
    //
    // We define one constant fake token string
    // In each test we:
    //   1. Send this token in Authorization header
    //   2. Mock jwtUtil.extractUserId(FAKE_TOKEN) → return 1
    //
    // This simulates a logged-in user with userId = 1
    // ─────────────────────────────────────────────────────────────
    private static final String FAKE_TOKEN  = "fake.jwt.token";
    private static final String AUTH_HEADER = "Bearer " + FAKE_TOKEN;

    // Shared response objects
    private UserResponseDTO      userResponse;
    private PurchaseLogResponseDTO purchase1;
    private PurchaseLogResponseDTO purchase2;

    @BeforeEach
    void setUp() {

        PermRoleResponseDTO role =
                new PermRoleResponseDTO(2, "RegisteredUser");

        userResponse = new UserResponseDTO();
        userResponse.setUserId(1);
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setUserName("johndoe");
        userResponse.setPhoneNumber("(123) 456-7890");
        userResponse.setRole(role);

        purchase1 = new PurchaseLogResponseDTO(
                1, 101, "John", "Doe", "johndoe");
        purchase2 = new PurchaseLogResponseDTO(
                1, 202, "John", "Doe", "johndoe");
    }

    // ─────────────────────────────────────────────────────────────
    // Helper method — sets up jwtUtil mock for userId = 1
    // We call this at the start of every test that needs JWT
    //
    // Why a helper? Because EVERY protected endpoint needs this
    // setup and repeating it in every test is messy
    // ─────────────────────────────────────────────────────────────
    private void mockJwtForUser(Integer userId) {
        when(jwtUtil.extractUserId(FAKE_TOKEN))
                .thenReturn(userId);
    }

    // ═════════════════════════════════════════════════════════════
    // GROUP 1 — Profile endpoints
    // ═════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /profile tests")
    class GetProfileTests {

        @Test
        @DisplayName("GET /profile → 200 with user profile")
        void getMyProfile_WhenValidToken_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            // Step 1: Mock JWT extraction → returns userId = 1
            mockJwtForUser(1);

            // Step 2: Mock service call
            when(userService.getMyProfile(1))
                    .thenReturn(userResponse);

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/user/profile")
                    // Send our fake token in the Authorization header
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Profile fetched successfully"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.userName").value("johndoe"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.role.permRole")
                        .value("RegisteredUser"));

            // Verify JWT was read and service was called with correct userId
            verify(jwtUtil, times(1)).extractUserId(FAKE_TOKEN);
            verify(userService, times(1)).getMyProfile(1);
        }

        @Test
        @DisplayName("GET /profile → 404 when user not found")
        void getMyProfile_WhenUserNotFound_ShouldReturn404()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(99);

            when(userService.getMyProfile(99))
                    .thenThrow(new ResourceNotFoundException(
                            "User", "userId", 99));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/user/profile")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404));
        }
    }

    // ═════════════════════════════════════════════════════════════
    // GROUP 2 — Update profile
    // ═════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PATCH /profile tests")
    class UpdateProfileTests {

        @Test
        @DisplayName("PATCH /profile → 200 when update is successful")
        void updateMyProfile_WhenValid_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            UserUpdateRequestDTO updateDTO =
                    new UserUpdateRequestDTO(
                            "UpdatedLast", "UpdatedFirst",
                            null, null);

            // Build updated response
            UserResponseDTO updatedResponse = new UserResponseDTO();
            updatedResponse.setUserId(1);
            updatedResponse.setFirstName("UpdatedFirst");
            updatedResponse.setLastName("UpdatedLast");
            updatedResponse.setUserName("johndoe");
            updatedResponse.setRole(
                    new PermRoleResponseDTO(2, "RegisteredUser"));

            when(userService.updateMyProfile(
                    eq(1), any(UserUpdateRequestDTO.class)))
                    .thenReturn(updatedResponse);

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    patch("/api/v1/user/profile")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Profile updated successfully"))
                .andExpect(jsonPath("$.data.firstName")
                        .value("UpdatedFirst"))
                .andExpect(jsonPath("$.data.lastName")
                        .value("UpdatedLast"));

            verify(userService, times(1))
                    .updateMyProfile(eq(1),
                            any(UserUpdateRequestDTO.class));
        }

        @Test
        @DisplayName("PATCH /profile → 409 when new username already taken")
        void updateMyProfile_WhenDuplicateUsername_ShouldReturn409()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            UserUpdateRequestDTO updateDTO =
                    new UserUpdateRequestDTO(
                            null, null, null, "takenname");

            when(userService.updateMyProfile(
                    eq(1), any(UserUpdateRequestDTO.class)))
                    .thenThrow(
                        new com.bookinventory.common.exception
                            .DuplicateResourceException(
                                "User", "userName", "takenname"));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    patch("/api/v1/user/profile")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDTO)))

                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers
                                .containsString("takenname")));
        }

        @Test
        @DisplayName("PUT /profile → 200 when PUT update is successful")
        void updateMyProfilePut_WhenValid_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            UserUpdateRequestDTO updateDTO =
                    new UserUpdateRequestDTO(
                            null, "PutFirst", null, null);

            UserResponseDTO updatedResponse = new UserResponseDTO();
            updatedResponse.setUserId(1);
            updatedResponse.setFirstName("PutFirst");
            updatedResponse.setLastName("Doe");
            updatedResponse.setUserName("johndoe");
            updatedResponse.setRole(
                    new PermRoleResponseDTO(2, "RegisteredUser"));

            when(userService.updateMyProfile(
                    eq(1), any(UserUpdateRequestDTO.class)))
                    .thenReturn(updatedResponse);

            // ── ACT + ASSERT ──────────────────────────────────────
            // Note: put() instead of patch()
            mockMvc.perform(
                    put("/api/v1/user/profile")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName")
                        .value("PutFirst"));
        }
    }

    // ═════════════════════════════════════════════════════════════
    // GROUP 3 — Change Password
    // ═════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PATCH /change-password tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("PATCH /change-password → 200 when password changed")
        void changePassword_WhenValid_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            ChangePasswordRequestDTO dto =
                    new ChangePasswordRequestDTO(
                            "pass1234", "newpass99", "newpass99");

            // changeMyPassword returns void — doNothing() is the mock
            doNothing().when(userService)
                    .changeMyPassword(eq(1),
                            any(ChangePasswordRequestDTO.class));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    patch("/api/v1/user/change-password")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Password changed successfully"));

            verify(userService, times(1))
                    .changeMyPassword(eq(1),
                            any(ChangePasswordRequestDTO.class));
        }

        @Test
        @DisplayName("PATCH /change-password → 400 when current password wrong")
        void changePassword_WhenWrongCurrentPassword_ShouldReturn400()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            ChangePasswordRequestDTO dto =
                    new ChangePasswordRequestDTO(
                            "wrongcurrent", "newpass99", "newpass99");

            doThrow(new BadRequestException("Current password is incorrect"))
                    .when(userService)
                    .changeMyPassword(eq(1),
                            any(ChangePasswordRequestDTO.class));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    patch("/api/v1/user/change-password")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Current password is incorrect"));
        }

        @Test
        @DisplayName("PATCH /change-password → 400 when new passwords mismatch")
        void changePassword_WhenPasswordsMismatch_ShouldReturn400()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            ChangePasswordRequestDTO dto =
                    new ChangePasswordRequestDTO(
                            "pass1234", "newpass99", "different99");

            doThrow(new BadRequestException(
                    "New password and confirm password do not match"))
                    .when(userService)
                    .changeMyPassword(eq(1),
                            any(ChangePasswordRequestDTO.class));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    patch("/api/v1/user/change-password")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("New password and confirm password"
                                + " do not match"));
        }
    }

    // ═════════════════════════════════════════════════════════════
    // GROUP 4 — Purchase history endpoints
    // ═════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Purchase history tests")
    class PurchaseTests {

        @Test
        @DisplayName("GET /purchases → 200 with purchase list")
        void getMyPurchases_WhenValid_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            when(purchaseLogService.getPurchasesByUser(1))
                    .thenReturn(Arrays.asList(purchase1, purchase2));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/user/purchases")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].inventoryId").value(101))
                .andExpect(jsonPath("$.data[1].inventoryId").value(202));
        }

        @Test
        @DisplayName("GET /purchases/count → 200 with count value")
        void getMyPurchaseCount_ShouldReturn200WithCount()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            when(purchaseLogService.getPurchaseCount(1))
                    .thenReturn(5L);

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/user/purchases/count")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5));
        }

        @Test
        @DisplayName("GET /purchases/inventory-ids → 200 with ID list")
        void getMyPurchasedInventoryIds_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            when(purchaseLogService.getInventoryIdsByUser(1))
                    .thenReturn(Arrays.asList(101, 202));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/user/purchases/inventory-ids")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0]").value(101))
                .andExpect(jsonPath("$.data[1]").value(202));
        }

        @Test
        @DisplayName("GET /purchases/check/{id} → 200 true when purchased")
        void checkPurchase_WhenPurchased_ShouldReturnTrue()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            when(purchaseLogService.hasPurchased(1, 101))
                    .thenReturn(true);

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    get("/api/v1/user/purchases/check/101")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User has already purchased this item"));
        }

        @Test
        @DisplayName("POST /purchases → 201 when purchase logged")
        void logPurchase_WhenValid_ShouldReturn201()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            // ─────────────────────────────────────────────────────
            // Note: userId in the DTO body does NOT matter here
            // The controller IGNORES dto.userId from request body
            // and REPLACES it with userId from JWT token
            //
            // So we only need to send inventoryId in the body
            // ─────────────────────────────────────────────────────
            PurchaseLogRequestDTO requestDTO =
                    new PurchaseLogRequestDTO(null, 303);

            when(purchaseLogService.addPurchase(
                    any(PurchaseLogRequestDTO.class)))
                    .thenReturn(new PurchaseLogResponseDTO(
                            1, 303, "John", "Doe", "johndoe"));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    post("/api/v1/user/purchases")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.inventoryId").value(303));
        }

        @Test
        @DisplayName("GET /purchases/{userId} → 200 when token userId matches path userId")
        void getPurchasesByUserId_WhenSameUser_ShouldReturn200()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            mockJwtForUser(1);

            when(purchaseLogService.getPurchasesByUser(1))
                    .thenReturn(Arrays.asList(purchase1, purchase2));

            // ── ACT + ASSERT ──────────────────────────────────────
            // Token userId (1) matches path userId (1) → allowed
            mockMvc.perform(
                    get("/api/v1/user/purchases/1")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
        }

        @Test
        @DisplayName("GET /purchases/{userId} → 403 when token userId doesn't match")
        void getPurchasesByUserId_WhenDifferentUser_ShouldReturn403()
                throws Exception {

            // ── ARRANGE ───────────────────────────────────────────
            // Token says userId = 1 but path says userId = 2
            // Controller throws ForbiddenException
            mockJwtForUser(1);

            when(purchaseLogService.getPurchasesByUser(2))
                    .thenThrow(new ForbiddenException(
                        "You are not allowed to view another"
                        + " user's purchases"));

            // ── ACT + ASSERT ──────────────────────────────────────
            mockMvc.perform(
                    // userId in path = 2, but JWT has userId = 1
                    get("/api/v1/user/purchases/2")
                    .header("Authorization", AUTH_HEADER)
                    .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers
                                .containsString("not allowed")));
        }
    }
}