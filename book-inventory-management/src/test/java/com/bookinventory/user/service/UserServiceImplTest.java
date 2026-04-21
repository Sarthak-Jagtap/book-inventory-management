package com.bookinventory.user.service;

import com.bookinventory.common.exception.BadRequestException;
import com.bookinventory.common.exception.DuplicateResourceException;
import com.bookinventory.common.exception.InvalidCredentialsException;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.user.dto.*;
import com.bookinventory.user.entity.PermRole;
import com.bookinventory.user.entity.PurchaseLog;
import com.bookinventory.user.entity.User;
import com.bookinventory.user.repository.PermRoleRepository;
import com.bookinventory.user.repository.PurchaseLogRepository;
import com.bookinventory.user.repository.UserRepository;
import com.bookinventory.user.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    // ── Four mocks — matches UserServiceImpl's constructor ────────────
    @Mock
    private UserRepository userRepository;

    @Mock
    private PermRoleRepository permRoleRepository;

    @Mock
    private PurchaseLogRepository purchaseLogRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    // ── Shared test data ──────────────────────────────────────────────
    private PermRole guestRole;
    private PermRole registeredUserRole;
    private PermRole storeOwnerRole;
    private PermRole adminRole;
    private User     sampleUser;

    @BeforeEach
    void setUp() {
        guestRole          = new PermRole(1, "Guest");
        registeredUserRole = new PermRole(2, "RegisteredUser");
        storeOwnerRole     = new PermRole(3, "StoreOwner");
        adminRole          = new PermRole(4, "Admin");

        // A fully built user — reused across many tests
        sampleUser = new User(
                "Doe", "John",
                "(123) 456-7890",
                "johndoe",
                "pass1234",
                registeredUserRole
        );
        sampleUser.setUserId(1);
    }

    // ═════════════════════════════════════════════════════════════════
    // GROUP 1 — registerUser()
    // ═════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("registerUser() tests")
    class RegisterTests {

        @Test
        @DisplayName("register → success with default role (RegisteredUser)")
        void register_WhenValid_NoRoleProvided_ShouldDefaultToRole2() {

            // ── ARRANGE ───────────────────────────────────────────────
            // DTO with no roleNumber — service should default to 2
            UserRequestDTO dto = new UserRequestDTO(
                    "Doe", "John",
                    "(123) 456-7890",
                    "johndoe", "pass1234",
                    null           // ← no role provided
            );

            // Username is NOT already taken
            when(userRepository.existsByUserName("johndoe"))
                    .thenReturn(false);

            // Role 2 (RegisteredUser) exists in DB
            when(permRoleRepository.findById(2))
                    .thenReturn(Optional.of(registeredUserRole));

            // When save() is called, return our sampleUser
            when(userRepository.save(any(User.class)))
                    .thenReturn(sampleUser);

            // ── ACT ───────────────────────────────────────────────────
            UserResponseDTO result = userService.registerUser(dto);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(1);
            assertThat(result.getUserName()).isEqualTo("johndoe");
            assertThat(result.getFirstName()).isEqualTo("John");
            assertThat(result.getLastName()).isEqualTo("Doe");

            // Role should be RegisteredUser (default)
            assertThat(result.getRole()).isNotNull();
            assertThat(result.getRole().getRoleNumber()).isEqualTo(2);
            assertThat(result.getRole().getPermRole()).isEqualTo("RegisteredUser");

            // Verify the service looked up role 2
            verify(permRoleRepository, times(1)).findById(2);
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("register → success with explicitly provided role (Admin assigns StoreOwner)")
        void register_WhenRoleProvided_ShouldUseProvidedRole() {

            // ── ARRANGE ───────────────────────────────────────────────
            UserRequestDTO dto = new UserRequestDTO(
                    "Smith", "Jane",
                    "(987) 654-3210",
                    "janesmith", "pass5678",
                    3              // ← role explicitly set to StoreOwner
            );

            User storeOwnerUser = new User(
                    "Smith", "Jane",
                    "(987) 654-3210",
                    "janesmith", "pass5678",
                    storeOwnerRole
            );
            storeOwnerUser.setUserId(2);

            when(userRepository.existsByUserName("janesmith"))
                    .thenReturn(false);

            when(permRoleRepository.findById(3))
                    .thenReturn(Optional.of(storeOwnerRole));

            when(userRepository.save(any(User.class)))
                    .thenReturn(storeOwnerUser);

            // ── ACT ───────────────────────────────────────────────────
            UserResponseDTO result = userService.registerUser(dto);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result.getRole().getRoleNumber()).isEqualTo(3);
            assertThat(result.getRole().getPermRole()).isEqualTo("StoreOwner");

            // It should look up role 3, NOT role 2
            verify(permRoleRepository, times(1)).findById(3);
        }

        @Test
        @DisplayName("register → throws DuplicateResourceException when username already taken")
        void register_WhenDuplicateUsername_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            UserRequestDTO dto = new UserRequestDTO(
                    "Doe", "John", null,
                    "johndoe",   // ← this username already exists
                    "pass1234", null
            );

            // Username IS already taken
            when(userRepository.existsByUserName("johndoe"))
                    .thenReturn(true);

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.registerUser(dto))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("johndoe");

            // If duplicate, we must NEVER reach save() or findById()
            verify(userRepository, never()).save(any());
            verify(permRoleRepository, never()).findById(any());
        }

        @Test
        @DisplayName("register → throws ResourceNotFoundException when role does not exist")
        void register_WhenRoleNotFound_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            UserRequestDTO dto = new UserRequestDTO(
                    "Doe", "John", null,
                    "johndoe", "pass1234",
                    99           // ← non-existent role
            );

            when(userRepository.existsByUserName("johndoe"))
                    .thenReturn(false);

            // Role 99 does not exist
            when(permRoleRepository.findById(99))
                    .thenReturn(Optional.empty());

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.registerUser(dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(userRepository, never()).save(any());
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // GROUP 2 — loginUser()
    // ═════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("loginUser() tests")
    class LoginTests {

        @Test
        @DisplayName("login → success with correct credentials returns token")
        void login_WhenValidCredentials_ShouldReturnLoginResponse() {

            // ── ARRANGE ───────────────────────────────────────────────
            LoginRequestDTO dto = new LoginRequestDTO("johndoe", "pass1234");

            // User found in DB
            when(userRepository.findByUserName("johndoe"))
                    .thenReturn(Optional.of(sampleUser));

            // Mock JWT generation
            // We don't want real JWT — just return a fake token string
            when(jwtUtil.generateToken("johndoe", 1, "RegisteredUser"))
                    .thenReturn("fake.jwt.token");

            // ── ACT ───────────────────────────────────────────────────
            LoginResponseDTO result = userService.loginUser(dto);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(1);
            assertThat(result.getUserName()).isEqualTo("johndoe");
            assertThat(result.getFirstName()).isEqualTo("John");
            assertThat(result.getLastName()).isEqualTo("Doe");
            assertThat(result.getRoleName()).isEqualTo("RegisteredUser");

            // Token should be our fake token
            assertThat(result.getToken()).isEqualTo("fake.jwt.token");
            assertThat(result.getTokenType()).isEqualTo("Bearer");

            // Login message
            assertThat(result.getMessage()).isEqualTo("Login successful");

            // Verify JWT was generated with correct parameters
            verify(jwtUtil, times(1))
                    .generateToken("johndoe", 1, "RegisteredUser");
        }

        @Test
        @DisplayName("login → throws InvalidCredentialsException when username not found")
        void login_WhenUsernameNotFound_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            LoginRequestDTO dto =
                    new LoginRequestDTO("unknownuser", "pass1234");

            when(userRepository.findByUserName("unknownuser"))
                    .thenReturn(Optional.empty());

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.loginUser(dto))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid username or password");

            // JWT should NEVER be generated if user not found
            verify(jwtUtil, never()).generateToken(any(), any(), any());
        }

        @Test
        @DisplayName("login → throws InvalidCredentialsException when password is wrong")
        void login_WhenWrongPassword_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            // User exists but password in DTO is wrong
            LoginRequestDTO dto =
                    new LoginRequestDTO("johndoe", "wrongpassword");

            when(userRepository.findByUserName("johndoe"))
                    .thenReturn(Optional.of(sampleUser));
            // sampleUser's password is "pass1234"
            // DTO sends "wrongpassword" → mismatch

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.loginUser(dto))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid username or password");

            verify(jwtUtil, never()).generateToken(any(), any(), any());
        }

        @Test
        @DisplayName("login → works even when user has no role assigned (defaults to Guest)")
        void login_WhenUserHasNoRole_ShouldDefaultToGuest() {

            // ── ARRANGE ───────────────────────────────────────────────
            // Build a user with NO role
            User noRoleUser = new User(
                    "Brown", "Bob", null,
                    "bobbrown", "pass1234",
                    null           // ← no role
            );
            noRoleUser.setUserId(3);

            LoginRequestDTO dto =
                    new LoginRequestDTO("bobbrown", "pass1234");

            when(userRepository.findByUserName("bobbrown"))
                    .thenReturn(Optional.of(noRoleUser));

            when(jwtUtil.generateToken("bobbrown", 3, "Guest"))
                    .thenReturn("guest.token");

            // ── ACT ───────────────────────────────────────────────────
            LoginResponseDTO result = userService.loginUser(dto);

            // ── ASSERT ────────────────────────────────────────────────
            // When role is null, service defaults to "Guest"
            assertThat(result.getRoleName()).isEqualTo("Guest");
            assertThat(result.getToken()).isEqualTo("guest.token");

            verify(jwtUtil, times(1))
                    .generateToken("bobbrown", 3, "Guest");
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // GROUP 3 — getMyProfile() and updateMyProfile()
    // ═════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Profile management tests")
    class ProfileTests {

        @Test
        @DisplayName("getMyProfile() → returns profile when user exists")
        void getMyProfile_WhenUserExists_ShouldReturnDTO() {

            // ── ARRANGE ───────────────────────────────────────────────
            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // ── ACT ───────────────────────────────────────────────────
            UserResponseDTO result = userService.getMyProfile(1);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(1);
            assertThat(result.getUserName()).isEqualTo("johndoe");
            assertThat(result.getPhoneNumber()).isEqualTo("(123) 456-7890");

            verify(userRepository, times(1)).findById(1);
        }

        @Test
        @DisplayName("getMyProfile() → throws ResourceNotFoundException when user not found")
        void getMyProfile_WhenUserNotFound_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            when(userRepository.findById(99))
                    .thenReturn(Optional.empty());

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.getMyProfile(99))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("updateMyProfile() → updates all provided fields successfully")
        void updateMyProfile_WhenAllFieldsProvided_ShouldUpdateAll() {

            // ── ARRANGE ───────────────────────────────────────────────
            UserUpdateRequestDTO dto = new UserUpdateRequestDTO(
                    "UpdatedLast",       // lastName
                    "UpdatedFirst",      // firstName
                    "(999) 888-7777",    // phoneNumber
                    "newusername"        // userName
            );

            // Build the "after update" user to return from save()
            User updatedUser = new User(
                    "UpdatedLast", "UpdatedFirst",
                    "(999) 888-7777",
                    "newusername", "pass1234",
                    registeredUserRole
            );
            updatedUser.setUserId(1);

            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // New username is NOT taken by anyone else
            when(userRepository.existsByUserName("newusername"))
                    .thenReturn(false);

            when(userRepository.save(any(User.class)))
                    .thenReturn(updatedUser);

            // ── ACT ───────────────────────────────────────────────────
            UserResponseDTO result = userService.updateMyProfile(1, dto);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result.getLastName()).isEqualTo("UpdatedLast");
            assertThat(result.getFirstName()).isEqualTo("UpdatedFirst");
            assertThat(result.getPhoneNumber()).isEqualTo("(999) 888-7777");
            assertThat(result.getUserName()).isEqualTo("newusername");

            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("updateMyProfile() → updates only non-null fields (partial update)")
        void updateMyProfile_WhenOnlyFirstNameProvided_ShouldUpdateOnlyFirstName() {

            // ── ARRANGE ───────────────────────────────────────────────
            // Only firstName is provided — rest are null
            UserUpdateRequestDTO dto = new UserUpdateRequestDTO(
                    null,       // lastName  ← not updating
                    "NewFirst", // firstName ← updating this
                    null,       // phone     ← not updating
                    null        // userName  ← not updating
            );

            // After save, only firstName changes
            User partiallyUpdatedUser = new User(
                    "Doe",      // lastName unchanged
                    "NewFirst", // firstName updated
                    "(123) 456-7890",
                    "johndoe",  // userName unchanged
                    "pass1234",
                    registeredUserRole
            );
            partiallyUpdatedUser.setUserId(1);

            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            when(userRepository.save(any(User.class)))
                    .thenReturn(partiallyUpdatedUser);

            // ── ACT ───────────────────────────────────────────────────
            UserResponseDTO result = userService.updateMyProfile(1, dto);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result.getFirstName()).isEqualTo("NewFirst");
            assertThat(result.getLastName()).isEqualTo("Doe"); // unchanged
            assertThat(result.getUserName()).isEqualTo("johndoe"); // unchanged

            // existsByUserName should NOT be called — we didn't change username
            verify(userRepository, never()).existsByUserName(anyString());
        }

        @Test
        @DisplayName("updateMyProfile() → throws DuplicateResourceException when username already taken")
        void updateMyProfile_WhenNewUsernameAlreadyTaken_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            UserUpdateRequestDTO dto = new UserUpdateRequestDTO(
                    null, null, null, "takenusername"
            );

            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // "takenusername" is already used by someone else
            when(userRepository.existsByUserName("takenusername"))
                    .thenReturn(true);

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.updateMyProfile(1, dto))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("takenusername");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("updateMyProfile() → allows keeping same username (no conflict)")
        void updateMyProfile_WhenSameUsername_ShouldNotThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            // User sends their CURRENT username — should be fine
            UserUpdateRequestDTO dto = new UserUpdateRequestDTO(
                    null, null, null, "johndoe" // same as current
            );

            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // existsByUserName returns true (it's in DB)
            // BUT the service checks: is it the SAME user? → yes → no conflict
            when(userRepository.existsByUserName("johndoe"))
                    .thenReturn(true);

            when(userRepository.save(any(User.class)))
                    .thenReturn(sampleUser);

            // ── ACT + ASSERT — no exception thrown ────────────────────
            UserResponseDTO result = userService.updateMyProfile(1, dto);

            assertThat(result).isNotNull();
            assertThat(result.getUserName()).isEqualTo("johndoe");

            // save() should be called (update went through)
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // GROUP 4 — changeMyPassword()
    // ═════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("changeMyPassword() tests")
    class PasswordTests {

        // ─────────────────────────────────────────────────────────────
        // changeMyPassword() has 3 validation rules:
        //   Rule 1: currentPassword must match DB password
        //   Rule 2: newPassword must match confirmPassword
        //   Rule 3: newPassword must be DIFFERENT from currentPassword
        //
        // We test each rule separately so we know EXACTLY which rule fails
        // ─────────────────────────────────────────────────────────────

        @Test
        @DisplayName("changePassword → success when all rules pass")
        void changePassword_WhenAllRulesPass_ShouldSucceed() {

            // ── ARRANGE ───────────────────────────────────────────────
            ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO(
                    "pass1234",  // currentPassword ← matches sampleUser's password
                    "newpass99", // newPassword      ← different from current
                    "newpass99"  // confirmPassword  ← matches newPassword
            );

            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // updatePassword returns 1 (1 row updated) — return value not used
            when(userRepository.updatePassword(1, "newpass99"))
                    .thenReturn(1);

            // ── ACT ───────────────────────────────────────────────────
            // changeMyPassword returns void — no result to capture
            // If no exception is thrown, the test passes
            userService.changeMyPassword(1, dto);

            // ── ASSERT ────────────────────────────────────────────────
            // Verify updatePassword was called with correct arguments
            verify(userRepository, times(1))
                    .updatePassword(1, "newpass99");
        }

        @Test
        @DisplayName("changePassword → throws BadRequestException when current password is wrong")
        void changePassword_WhenCurrentPasswordWrong_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO(
                    "wrongcurrent", // ← wrong! actual is "pass1234"
                    "newpass99",
                    "newpass99"
            );

            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.changeMyPassword(1, dto))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Current password is incorrect");

            // updatePassword should NEVER be called if validation fails
            verify(userRepository, never()).updatePassword(anyInt(), anyString());
        }

        @Test
        @DisplayName("changePassword → throws BadRequestException when passwords do not match")
        void changePassword_WhenPasswordsMismatch_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO(
                    "pass1234",    // correct current
                    "newpass99",   // newPassword
                    "different99"  // ← confirmPassword doesn't match!
            );

            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.changeMyPassword(1, dto))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("New password and confirm password do not match");

            verify(userRepository, never()).updatePassword(anyInt(), anyString());
        }

        @Test
        @DisplayName("changePassword → throws BadRequestException when new = current password")
        void changePassword_WhenNewPasswordSameAsCurrent_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            // newPassword is the SAME as currentPassword — not allowed
            ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO(
                    "pass1234", // currentPassword
                    "pass1234", // newPassword     ← same as current!
                    "pass1234"  // confirmPassword ← matches new (rule 2 passes)
            );

            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.changeMyPassword(1, dto))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("New password must be different from current password");

            verify(userRepository, never()).updatePassword(anyInt(), anyString());
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // GROUP 5 — Admin operations
    // ═════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Admin operations tests")
    class AdminTests {

        @Test
        @DisplayName("getAllUsers() → returns all users mapped to DTOs")
        void getAllUsers_ShouldReturnAllUsers() {

            // ── ARRANGE ───────────────────────────────────────────────
            User user2 = new User(
                    "Smith", "Jane",
                    "(987) 654-3210",
                    "janesmith", "pass5678",
                    adminRole
            );
            user2.setUserId(2);

            when(userRepository.findAllUsersWithRole())
                    .thenReturn(Arrays.asList(sampleUser, user2));

            // ── ACT ───────────────────────────────────────────────────
            List<UserResponseDTO> result = userService.getAllUsers();

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result).hasSize(2);

            assertThat(result.get(0).getUserId()).isEqualTo(1);
            assertThat(result.get(0).getUserName()).isEqualTo("johndoe");

            assertThat(result.get(1).getUserId()).isEqualTo(2);
            assertThat(result.get(1).getUserName()).isEqualTo("janesmith");

            verify(userRepository, times(1)).findAllUsersWithRole();
        }

        @Test
        @DisplayName("getAllUsers() → returns empty list when no users exist")
        void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {

            // ── ARRANGE ───────────────────────────────────────────────
            when(userRepository.findAllUsersWithRole())
                    .thenReturn(Collections.emptyList());

            // ── ACT ───────────────────────────────────────────────────
            List<UserResponseDTO> result = userService.getAllUsers();

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("getUserById() → returns correct user when found")
        void getUserById_WhenUserExists_ShouldReturnDTO() {

            // ── ARRANGE ───────────────────────────────────────────────
            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // ── ACT ───────────────────────────────────────────────────
            UserResponseDTO result = userService.getUserById(1);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(1);
            assertThat(result.getUserName()).isEqualTo("johndoe");
        }

        @Test
        @DisplayName("getUserById() → throws ResourceNotFoundException when not found")
        void getUserById_WhenUserNotFound_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            when(userRepository.findById(99))
                    .thenReturn(Optional.empty());

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.getUserById(99))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("updateUserById() → admin can update any user's details")
        void updateUserById_WhenValid_ShouldReturnUpdatedDTO() {

            // ── ARRANGE ───────────────────────────────────────────────
            UserUpdateRequestDTO dto = new UserUpdateRequestDTO(
                    "ChangedLast", "ChangedFirst", null, null
            );

            User updatedUser = new User(
                    "ChangedLast", "ChangedFirst",
                    "(123) 456-7890",
                    "johndoe", "pass1234",
                    registeredUserRole
            );
            updatedUser.setUserId(1);

            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            when(userRepository.save(any(User.class)))
                    .thenReturn(updatedUser);

            // ── ACT ───────────────────────────────────────────────────
            UserResponseDTO result = userService.updateUserById(1, dto);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result.getLastName()).isEqualTo("ChangedLast");
            assertThat(result.getFirstName()).isEqualTo("ChangedFirst");
            assertThat(result.getUserName()).isEqualTo("johndoe"); // unchanged

            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("updateUserRole() → admin successfully changes user role")
        void updateUserRole_WhenBothExist_ShouldReturnUpdatedDTO() {

            // ── ARRANGE ───────────────────────────────────────────────
            // Build user who will receive the new role
            User promotedUser = new User(
                    "Doe", "John",
                    "(123) 456-7890",
                    "johndoe", "pass1234",
                    storeOwnerRole   // ← new role after promotion
            );
            promotedUser.setUserId(1);

            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // Role 3 (StoreOwner) exists
            when(permRoleRepository.findById(3))
                    .thenReturn(Optional.of(storeOwnerRole));

            when(userRepository.save(any(User.class)))
                    .thenReturn(promotedUser);

            // ── ACT ───────────────────────────────────────────────────
            UserResponseDTO result = userService.updateUserRole(1, 3);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result.getRole().getRoleNumber()).isEqualTo(3);
            assertThat(result.getRole().getPermRole()).isEqualTo("StoreOwner");

            verify(permRoleRepository, times(1)).findById(3);
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("updateUserRole() → throws exception when new role does not exist")
        void updateUserRole_WhenRoleNotFound_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            when(userRepository.findById(1))
                    .thenReturn(Optional.of(sampleUser));

            // Role 99 doesn't exist
            when(permRoleRepository.findById(99))
                    .thenReturn(Optional.empty());

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.updateUserRole(1, 99))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("updateUserRole() → throws exception when user does not exist")
        void updateUserRole_WhenUserNotFound_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            when(userRepository.findById(99))
                    .thenReturn(Optional.empty());

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.updateUserRole(99, 2))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            // Role lookup should NEVER happen if user not found
            verify(permRoleRepository, never()).findById(any());
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // GROUP 6 — getUsersByRole() (shared utility)
    // ═════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getUsersByRole() tests")
    class GetUsersByRoleTests {

        @Test
        @DisplayName("getUsersByRole() → returns users with matching role")
        void getUsersByRole_WhenRoleExists_ShouldReturnUsers() {

            // ── ARRANGE ───────────────────────────────────────────────
            User user2 = new User(
                    "Smith", "Jane", null,
                    "janesmith", "pass5678",
                    registeredUserRole
            );
            user2.setUserId(2);

            // Role 2 exists
            when(permRoleRepository.existsById(2)).thenReturn(true);

            // Two users with role 2
            when(userRepository.findByRole_RoleNumber(2))
                    .thenReturn(Arrays.asList(sampleUser, user2));

            // ── ACT ───────────────────────────────────────────────────
            List<UserResponseDTO> result = userService.getUsersByRole(2);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getUserName()).isEqualTo("johndoe");
            assertThat(result.get(1).getUserName()).isEqualTo("janesmith");

            verify(permRoleRepository, times(1)).existsById(2);
            verify(userRepository, times(1)).findByRole_RoleNumber(2);
        }

        @Test
        @DisplayName("getUsersByRole() → throws exception when role does not exist")
        void getUsersByRole_WhenRoleNotFound_ShouldThrowException() {

            // ── ARRANGE ───────────────────────────────────────────────
            when(permRoleRepository.existsById(99)).thenReturn(false);

            // ── ASSERT + ACT ──────────────────────────────────────────
            assertThatThrownBy(() -> userService.getUsersByRole(99))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            // Users should never be queried if role doesn't exist
            verify(userRepository, never()).findByRole_RoleNumber(any());
        }

        @Test
        @DisplayName("getUsersByRole() → returns empty list when role has no users")
        void getUsersByRole_WhenNoUsersForRole_ShouldReturnEmptyList() {

            // ── ARRANGE ───────────────────────────────────────────────
            when(permRoleRepository.existsById(4)).thenReturn(true);

            when(userRepository.findByRole_RoleNumber(4))
                    .thenReturn(Collections.emptyList());

            // ── ACT ───────────────────────────────────────────────────
            List<UserResponseDTO> result = userService.getUsersByRole(4);

            // ── ASSERT ────────────────────────────────────────────────
            assertThat(result).isEmpty();
        }
    }
}