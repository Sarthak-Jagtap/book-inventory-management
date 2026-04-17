package com.bookinventory.user.controller;

import com.bookinventory.common.exception.DuplicateResourceException;
import com.bookinventory.common.exception.InvalidCredentialsException;
import com.bookinventory.user.common.config.JwtAuthFilter;
import com.bookinventory.user.dto.LoginRequestDTO;
import com.bookinventory.user.dto.LoginResponseDTO;
import com.bookinventory.user.dto.PermRoleResponseDTO;
import com.bookinventory.user.dto.UserRequestDTO;
import com.bookinventory.user.dto.UserResponseDTO;
import com.bookinventory.user.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    value = AuthController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthFilter.class
    )
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // ── Shared test data ──────────────────────────────────────────
    private UserResponseDTO registeredUserResponse;
    private LoginResponseDTO loginResponse;

    @BeforeEach
    void setUp() {

        // What the service returns after successful registration
        PermRoleResponseDTO role =
                new PermRoleResponseDTO(2, "RegisteredUser");

        registeredUserResponse = new UserResponseDTO();
        registeredUserResponse.setUserId(1);
        registeredUserResponse.setFirstName("John");
        registeredUserResponse.setLastName("Doe");
        registeredUserResponse.setUserName("johndoe");
        registeredUserResponse.setPhoneNumber("(123) 456-7890");
        registeredUserResponse.setRole(role);

        // What the service returns after successful login
        loginResponse = new LoginResponseDTO();
        loginResponse.setUserId(1);
        loginResponse.setUserName("johndoe");
        loginResponse.setFirstName("John");
        loginResponse.setLastName("Doe");
        loginResponse.setRoleName("RegisteredUser");
        loginResponse.setMessage("Login successful");
        loginResponse.setToken("fake.jwt.token");
        loginResponse.setTokenType("Bearer");
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: POST /api/v1/auth/register
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /register → 201 when registration is successful")
    void register_WhenValid_ShouldReturn201() throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        UserRequestDTO requestDTO = new UserRequestDTO(
                "Doe", "John",
                "(123) 456-7890",
                "johndoe", "pass1234",
                null
        );

        when(userService.registerUser(any(UserRequestDTO.class)))
                .thenReturn(registeredUserResponse);

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))

            // 201 CREATED
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.message")
                    .value("User registered successfully"))

            // User data in response
            .andExpect(jsonPath("$.data.userId").value(1))
            .andExpect(jsonPath("$.data.userName").value("johndoe"))
            .andExpect(jsonPath("$.data.firstName").value("John"))
            .andExpect(jsonPath("$.data.lastName").value("Doe"))
            .andExpect(jsonPath("$.data.phoneNumber")
                    .value("(123) 456-7890"))

            // Role data nested in response
            .andExpect(jsonPath("$.data.role.roleNumber").value(2))
            .andExpect(jsonPath("$.data.role.permRole")
                    .value("RegisteredUser"));

        verify(userService, times(1))
                .registerUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /register → 409 when username already taken")
    void register_WhenDuplicateUsername_ShouldReturn409()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        UserRequestDTO requestDTO = new UserRequestDTO(
                "Doe", "John", null,
                "johndoe", "pass1234", null
        );

        when(userService.registerUser(any(UserRequestDTO.class)))
                .thenThrow(new DuplicateResourceException(
                        "User", "userName", "johndoe"));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))

            // 409 CONFLICT
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.message")
                    .value(org.hamcrest.Matchers
                            .containsString("johndoe")));
    }

    @Test
    @DisplayName("POST /register → 400 when required fields are missing")
    void register_WhenMissingRequiredFields_ShouldReturn400()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        // lastName and firstName are @NotBlank — sending blanks
        UserRequestDTO invalidDTO = new UserRequestDTO(
                "",    // lastName  ← blank, violates @NotBlank
                "",    // firstName ← blank, violates @NotBlank
                null,
                "",    // userName  ← blank, violates @NotBlank
                "",    // password  ← blank, violates @NotBlank
                null
        );

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))

            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                    .value("Validation failed"));

        // Service must NEVER be called if validation fails
        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("POST /register → 400 when phone number format is wrong")
    void register_WhenInvalidPhoneFormat_ShouldReturn400()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        // Phone must match (XXX) XXX-XXXX
        UserRequestDTO invalidDTO = new UserRequestDTO(
                "Doe", "John",
                "1234567890",   // ← wrong format, violates @Pattern
                "johndoe", "pass1234",
                null
        );

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))

            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));

        verify(userService, never()).registerUser(any());
    }

    // ═════════════════════════════════════════════════════════════
    // Tests for: POST /api/v1/auth/login
    // ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /login → 200 with token when credentials are valid")
    void login_WhenValidCredentials_ShouldReturn200WithToken()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        LoginRequestDTO requestDTO =
                new LoginRequestDTO("johndoe", "pass1234");

        when(userService.loginUser(any(LoginRequestDTO.class)))
                .thenReturn(loginResponse);

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))

            // 200 OK
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200))

            // Full login response data
            .andExpect(jsonPath("$.data.userId").value(1))
            .andExpect(jsonPath("$.data.userName").value("johndoe"))
            .andExpect(jsonPath("$.data.firstName").value("John"))
            .andExpect(jsonPath("$.data.roleName")
                    .value("RegisteredUser"))
            .andExpect(jsonPath("$.data.token")
                    .value("fake.jwt.token"))
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.data.message")
                    .value("Login successful"));

        verify(userService, times(1))
                .loginUser(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /login → 401 when credentials are invalid")
    void login_WhenInvalidCredentials_ShouldReturn401()
            throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        LoginRequestDTO requestDTO =
                new LoginRequestDTO("johndoe", "wrongpassword");

        when(userService.loginUser(any(LoginRequestDTO.class)))
                .thenThrow(new InvalidCredentialsException(
                        "Invalid username or password"));

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))

            // 401 UNAUTHORIZED
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message")
                    .value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /login → 400 when username or password is blank")
    void login_WhenBlankFields_ShouldReturn400() throws Exception {

        // ── ARRANGE ───────────────────────────────────────────────
        // Both fields @NotBlank
        LoginRequestDTO invalidDTO =
                new LoginRequestDTO("", "");

        // ── ACT + ASSERT ──────────────────────────────────────────
        mockMvc.perform(
                post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))

            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                    .value("Validation failed"));

        verify(userService, never()).loginUser(any());
    }
}