package com.bookinventory.user.controller;

import com.bookinventory.user.dto.LoginRequestDTO;
import com.bookinventory.user.dto.LoginResponseDTO;
import com.bookinventory.user.dto.UserRequestDTO;
import com.bookinventory.user.dto.UserResponseDTO;
import com.bookinventory.user.response.ApiResponse;
import com.bookinventory.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	// REGISTER
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserResponseDTO>> register(@Valid @RequestBody UserRequestDTO dto) {

		UserResponseDTO registered = userService.registerUser(dto);

		return new ResponseEntity<>(ApiResponse.success(201, "User registered successfully", registered),
				HttpStatus.CREATED);
	}

	// LOGIN
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {

		LoginResponseDTO response = userService.loginUser(dto);

		return new ResponseEntity<>(ApiResponse.success(200, response.getMessage(), response), HttpStatus.OK);
	}
}