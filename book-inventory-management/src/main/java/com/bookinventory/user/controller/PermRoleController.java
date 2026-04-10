package com.bookinventory.user.controller;

import com.bookinventory.user.common.response.ApiResponse;
import com.bookinventory.user.dto.PermRoleResponseDTO;
import com.bookinventory.user.service.PermRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class PermRoleController {

    private final PermRoleService permRoleService;

    // Constructor injection
    public PermRoleController(PermRoleService permRoleService) {
        this.permRoleService = permRoleService;
    }

    // ─────────────────────────────────────────────────────────────────
    // GET ALL ROLES
    // GET /api/roles
    // ─────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermRoleResponseDTO>>> getAllRoles() {

        List<PermRoleResponseDTO> roles = permRoleService.getAllRoles();

        return new ResponseEntity<>(
                ApiResponse.success("Roles fetched successfully", roles),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // GET ROLE BY ID
    // GET /api/roles/1
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/{roleNumber}")
    public ResponseEntity<ApiResponse<PermRoleResponseDTO>> getRoleById(
            @PathVariable Integer roleNumber) {

        PermRoleResponseDTO role = permRoleService.getRoleById(roleNumber);

        return new ResponseEntity<>(
                ApiResponse.success("Role fetched successfully", role),
                HttpStatus.OK
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // GET ROLE BY NAME
    // GET /api/roles/name/Admin
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/name/{permRole}")
    public ResponseEntity<ApiResponse<PermRoleResponseDTO>> getRoleByName(
            @PathVariable String permRole) {

        PermRoleResponseDTO role = permRoleService.getRoleByName(permRole);

        return new ResponseEntity<>(
                ApiResponse.success("Role fetched successfully", role),
                HttpStatus.OK
        );
    }
}