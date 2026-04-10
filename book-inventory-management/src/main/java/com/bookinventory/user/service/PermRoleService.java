package com.bookinventory.user.service;

import com.bookinventory.user.dto.PermRoleResponseDTO;
import java.util.List;

public interface PermRoleService {

    // Get all roles
    List<PermRoleResponseDTO> getAllRoles();

    // Get one role by its ID
    PermRoleResponseDTO getRoleById(Integer roleNumber);

    // Get one role by its name
    PermRoleResponseDTO getRoleByName(String permRole);
}