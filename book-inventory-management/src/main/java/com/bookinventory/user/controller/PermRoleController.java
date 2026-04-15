package com.bookinventory.user.controller;

import com.bookinventory.user.dto.PermRoleResponseDTO;
import com.bookinventory.user.response.ApiResponse;
import com.bookinventory.user.service.PermRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class PermRoleController {

    private final PermRoleService permRoleService;

    public PermRoleController(PermRoleService permRoleService) {
        this.permRoleService = permRoleService;
    }

    /** GET /api/v1/roles — Public */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermRoleResponseDTO>>> getAllRoles() {
        return new ResponseEntity<>(
                ApiResponse.success(200, "Roles fetched successfully", permRoleService.getAllRoles()),
                HttpStatus.OK
        );
    }

    /** GET /api/v1/roles/{roleNumber} — Public */
    @GetMapping("/{roleNumber}")
    public ResponseEntity<ApiResponse<PermRoleResponseDTO>> getRoleById(
            @PathVariable Integer roleNumber) {

        return new ResponseEntity<>(
                ApiResponse.success(200, "Role fetched successfully", permRoleService.getRoleById(roleNumber)),
                HttpStatus.OK
        );
    }
}