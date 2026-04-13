package com.bookinventory.user.common.exception;

import com.bookinventory.user.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 : Resource not found ───────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        ApiResponse<Object> response = ApiResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // ── 409 : Duplicate resource ───────────────────────────────────────
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResource(
            DuplicateResourceException ex) {

        ApiResponse<Object> response = ApiResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // ── 400 : Bad request (custom logic) ──────────────────────────────
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            BadRequestException ex) {

        ApiResponse<Object> response = ApiResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ── 401 : Invalid credentials ─────────────────────────────────────
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        ApiResponse<Object> response = ApiResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // ── 400 : @Valid annotation failures (field-level validation) ──────
    // This handles errors from @NotBlank, @Size, @Pattern etc. in your DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // Loop through all field errors and collect them
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiResponse<Map<String, String>> response =
                new ApiResponse<>(false, "Validation failed", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ── 500 : Any unexpected exception ────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {

        ApiResponse<Object> response =
                ApiResponse.failure("An unexpected error occurred: " + ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFound(
            org.springframework.web.servlet.resource.NoResourceFoundException ex) {

        ApiResponse<Object> response =
                ApiResponse.failure("API endpoint not found: " + ex.getResourcePath());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}