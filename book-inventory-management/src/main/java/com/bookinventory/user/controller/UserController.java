package com.bookinventory.user.controller;

import com.bookinventory.user.common.response.ApiResponse;
import com.bookinventory.user.dto.ChangePasswordRequestDTO;
import com.bookinventory.user.dto.LoginRequestDTO;
import com.bookinventory.user.dto.LoginResponseDTO;
import com.bookinventory.user.dto.UserRequestDTO;
import com.bookinventory.user.dto.UserResponseDTO;
import com.bookinventory.user.dto.UserUpdateRequestDTO;
import com.bookinventory.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ─────────────────────────────────────────────────────────────────
    // REGISTER NEW USER
    // POST /api/users/register
    // Body: { lastName, firstName, phoneNumber, userName, password, roleNumber }
    // ─────────────────────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(
            @Valid @RequestBody UserRequestDTO userRequestDTO) {

        UserResponseDTO registeredUser = userService.registerUser(userRequestDTO);

        return new ResponseEntity<>(
                ApiResponse.success("User registered successfully", registeredUser),
                HttpStatus.CREATED   // 201
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // LOGIN
    // POST /api/users/login
    // Body: { userName, password }
    // ─────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginUser(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {

        LoginResponseDTO loginResponse = userService.loginUser(loginRequestDTO);

        return new ResponseEntity<>(
                ApiResponse.success(loginResponse.getMessage(), loginResponse),
                HttpStatus.OK   // 200
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // GET ALL USERS
    // GET /api/users
    // ─────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {

        List<UserResponseDTO> users = userService.getAllUsers();

        return new ResponseEntity<>(
                ApiResponse.success("Users fetched successfully", users),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // GET USER BY ID
    // GET /api/users/5
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(
            @PathVariable Integer userId) {

        UserResponseDTO user = userService.getUserById(userId);

        return new ResponseEntity<>(
                ApiResponse.success("User fetched successfully", user),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // GET USER BY USERNAME
    // GET /api/users/username/krishna123
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/username/{userName}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByUsername(
            @PathVariable String userName) {

        UserResponseDTO user = userService.getUserByUsername(userName);

        return new ResponseEntity<>(
                ApiResponse.success("User fetched successfully", user),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // GET ALL USERS BY ROLE
    // GET /api/users/role/1
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/role/{roleNumber}")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsersByRole(
            @PathVariable Integer roleNumber) {

        List<UserResponseDTO> users = userService.getUsersByRole(roleNumber);

        return new ResponseEntity<>(
                ApiResponse.success("Users fetched successfully", users),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // UPDATE USER PROFILE
    // PUT /api/users/5
    // Body: { lastName, firstName, phoneNumber, userName }
    // ─────────────────────────────────────────────────────────────────
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UserUpdateRequestDTO updateDTO) {

        UserResponseDTO updatedUser = userService.updateUser(userId, updateDTO);

        return new ResponseEntity<>(
                ApiResponse.success("User updated successfully", updatedUser),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // CHANGE PASSWORD
    // PUT /api/users/5/change-password
    // Body: { currentPassword, newPassword, confirmPassword }
    // ─────────────────────────────────────────────────────────────────
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @PathVariable Integer userId,
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordDTO) {

        userService.changePassword(userId, changePasswordDTO);

        return new ResponseEntity<>(
                ApiResponse.success("Password changed successfully"),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // UPDATE USER ROLE  (Admin operation)
    // PUT /api/users/5/role/2
    // ─────────────────────────────────────────────────────────────────
    @PutMapping("/{userId}/role/{roleNumber}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserRole(
            @PathVariable Integer userId,
            @PathVariable Integer roleNumber) {

        UserResponseDTO updatedUser = userService.updateUserRole(userId, roleNumber);

        return new ResponseEntity<>(
                ApiResponse.success("User role updated successfully", updatedUser),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // DELETE USER
    // DELETE /api/users/5
    // ─────────────────────────────────────────────────────────────────
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(
            @PathVariable Integer userId) {

        userService.deleteUser(userId);

        return new ResponseEntity<>(
                ApiResponse.success("User deleted successfully"),
                HttpStatus.OK
        );
    }
}