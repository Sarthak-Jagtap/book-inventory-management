package com.bookinventory.user.service;

import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.user.dto.PermRoleResponseDTO;
import com.bookinventory.user.entity.PermRole;
import com.bookinventory.user.repository.PermRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

// ─────────────────────────────────────────────────────────────
// @ExtendWith(MockitoExtension.class)
//   → Tells JUnit 5: "use Mockito to handle @Mock and @InjectMocks"
//   → Without this, your mocks will be null and tests will crash
// ─────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class PermRoleServiceImplTest {

    // ─────────────────────────────────────────────────────────
    // @Mock
    //   → Creates a FAKE PermRoleRepository
    //   → It does NOT talk to any real database
    //   → We will tell it exactly what to return in each test
    // ─────────────────────────────────────────────────────────
    @Mock
    private PermRoleRepository permRoleRepository;

    // ─────────────────────────────────────────────────────────
    // @InjectMocks
    //   → Creates a REAL PermRoleServiceImpl object
    //   → Automatically injects the @Mock above into it
    //   → So when the service calls permRoleRepository.findAll(),
    //     it calls OUR fake, not a real DB
    // ─────────────────────────────────────────────────────────
    @InjectMocks
    private PermRoleServiceImpl permRoleService;

    // ─────────────────────────────────────────────────────────
    // Sample data we reuse across multiple tests
    // Think of these as "test fixtures" — pre-built objects
    // ─────────────────────────────────────────────────────────
    private PermRole guestRole;
    private PermRole registeredUserRole;
    private PermRole storeOwnerRole;
    private PermRole adminRole;

    // ─────────────────────────────────────────────────────────
    // @BeforeEach
    //   → This method runs BEFORE every single @Test method
    //   → Use it to set up fresh test data each time
    //   → Prevents one test from affecting another
    // ─────────────────────────────────────────────────────────
    @BeforeEach
    void setUp() {
        guestRole        = new PermRole(1, "Guest");
        registeredUserRole = new PermRole(2, "RegisteredUser");
        storeOwnerRole   = new PermRole(3, "StoreOwner");
        adminRole        = new PermRole(4, "Admin");
    }

    // ═════════════════════════════════════════════════════════
    // Tests for: getAllRoles()
    // ═════════════════════════════════════════════════════════

    @Test
    // @DisplayName gives a human-readable name shown in test results
    @DisplayName("getAllRoles() → returns all 4 roles as DTOs")
    void getAllRoles_ShouldReturnAllRoles() {

        // ── ARRANGE ───────────────────────────────────────────
        // "Arrange" = set up the scenario
        //
        // We tell our FAKE repository:
        //   "When someone calls findAll(), return this list"
        //
        // This is the core Mockito pattern:
        //   when(mock.method()).thenReturn(value)
        List<PermRole> fakeRoles = Arrays.asList(
                guestRole, registeredUserRole, storeOwnerRole, adminRole);

        when(permRoleRepository.findAll()).thenReturn(fakeRoles);

        // ── ACT ───────────────────────────────────────────────
        // "Act" = call the actual method we are testing
        List<PermRoleResponseDTO> result = permRoleService.getAllRoles();

        // ── ASSERT ────────────────────────────────────────────
        // "Assert" = verify the result is what we expected
        //
        // assertThat() is from AssertJ — it gives readable error messages
        // if a test fails

        // We got 4 roles back
        assertThat(result).hasSize(4);

        // First role has correct data
        assertThat(result.get(0).getRoleNumber()).isEqualTo(1);
        assertThat(result.get(0).getPermRole()).isEqualTo("Guest");

        // Last role is Admin
        assertThat(result.get(3).getRoleNumber()).isEqualTo(4);
        assertThat(result.get(3).getPermRole()).isEqualTo("Admin");

        // verify() checks: was findAll() actually called exactly once?
        // This confirms the service didn't skip the DB call entirely
        verify(permRoleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllRoles() → returns empty list when no roles exist")
    void getAllRoles_WhenNoRolesExist_ShouldReturnEmptyList() {

        // ARRANGE — fake an empty database
        when(permRoleRepository.findAll()).thenReturn(Arrays.asList());

        // ACT
        List<PermRoleResponseDTO> result = permRoleService.getAllRoles();

        // ASSERT
        assertThat(result).isEmpty();
        verify(permRoleRepository, times(1)).findAll();
    }

    // ═════════════════════════════════════════════════════════
    // Tests for: getRoleById()
    // ═════════════════════════════════════════════════════════

    @Test
    @DisplayName("getRoleById() → returns correct DTO when role exists")
    void getRoleById_WhenRoleExists_ShouldReturnDTO() {

        // ARRANGE
        // Optional.of(...) means "the DB found this record"
        when(permRoleRepository.findById(2))
                .thenReturn(Optional.of(registeredUserRole));

        // ACT
        PermRoleResponseDTO result = permRoleService.getRoleById(2);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getRoleNumber()).isEqualTo(2);
        assertThat(result.getPermRole()).isEqualTo("RegisteredUser");

        verify(permRoleRepository, times(1)).findById(2);
    }

    @Test
    @DisplayName("getRoleById() → throws ResourceNotFoundException when role not found")
    void getRoleById_WhenRoleNotFound_ShouldThrowException() {

        // ARRANGE
        // Optional.empty() means "the DB found nothing"
        when(permRoleRepository.findById(99))
                .thenReturn(Optional.empty());

        // ASSERT + ACT (combined when testing exceptions)
        //
        // assertThatThrownBy(() -> ...) 
        //   → runs the lambda, expects it to throw
        //   → then we check the exception type and message
        assertThatThrownBy(() -> permRoleService.getRoleById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(permRoleRepository, times(1)).findById(99);
    }

    // ═════════════════════════════════════════════════════════
    // Tests for: getRoleByName()
    // ═════════════════════════════════════════════════════════

    @Test
    @DisplayName("getRoleByName() → returns correct DTO when role name exists")
    void getRoleByName_WhenRoleExists_ShouldReturnDTO() {

        // ARRANGE
        when(permRoleRepository.findByPermRole("Admin"))
                .thenReturn(Optional.of(adminRole));

        // ACT
        PermRoleResponseDTO result = permRoleService.getRoleByName("Admin");

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getRoleNumber()).isEqualTo(4);
        assertThat(result.getPermRole()).isEqualTo("Admin");

        verify(permRoleRepository, times(1)).findByPermRole("Admin");
    }

    @Test
    @DisplayName("getRoleByName() → throws ResourceNotFoundException when name not found")
    void getRoleByName_WhenRoleNotFound_ShouldThrowException() {

        // ARRANGE
        when(permRoleRepository.findByPermRole("SuperAdmin"))
                .thenReturn(Optional.empty());

        // ASSERT + ACT
        assertThatThrownBy(() -> permRoleService.getRoleByName("SuperAdmin"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SuperAdmin");

        verify(permRoleRepository, times(1)).findByPermRole("SuperAdmin");
    }
}