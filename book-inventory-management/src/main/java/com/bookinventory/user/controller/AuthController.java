package com.bookinventory.user.controller;

import com.bookinventory.user.dto.LoginRequestDTO;
import com.bookinventory.user.dto.LoginResponseDTO;
import com.bookinventory.user.dto.TokenValidationResponseDTO;
import com.bookinventory.user.dto.UserRequestDTO;
import com.bookinventory.user.dto.UserResponseDTO;
import com.bookinventory.user.response.ApiResponse;
import com.bookinventory.user.service.UserService;
import com.bookinventory.user.util.JwtUtil;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final UserService userService;
	private final JwtUtil jwtUtil;

	public AuthController(UserService userService, JwtUtil jwtUtil) {
		this.userService = userService;
		this.jwtUtil = jwtUtil;
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
	
	// POST /api/v1/auth/validate-token
	// Request body: { "token": "eyJhbG..." }
	// This endpoint tells the frontend whether a token is still valid.
	// The frontend stores the token in session and can call this to check.
	@PostMapping("/validate-token")
	public ResponseEntity<ApiResponse<TokenValidationResponseDTO>> validateToken(
	        @RequestBody java.util.Map<String, String> body) {

	    String token = body.get("token");
	    TokenValidationResponseDTO result = new TokenValidationResponseDTO();

	    if (token == null || token.isBlank()) {
	        result.setValid(false);
	        result.setMessage("Token is missing");
	        return ResponseEntity.ok(ApiResponse.success(200, "Token validation result", result));
	    }

	    if (!jwtUtil.isTokenValid(token)) {
	        result.setValid(false);
	        result.setMessage("Token is expired or invalid");
	        return ResponseEntity.ok(ApiResponse.success(200, "Token validation result", result));
	    }

	    // Token is valid — extract all info from it
	    result.setValid(true);
	    result.setUserName(jwtUtil.extractUserName(token));
	    result.setUserId(jwtUtil.extractUserId(token));
	    result.setRoleName(jwtUtil.extractRoleName(token));
	    result.setMessage("Token is valid");

	    return ResponseEntity.ok(ApiResponse.success(200, "Token validation result", result));
	}
}