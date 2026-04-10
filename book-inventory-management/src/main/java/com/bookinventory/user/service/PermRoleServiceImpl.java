package com.bookinventory.user.service;

import com.bookinventory.user.common.exception.ResourceNotFoundException;
import com.bookinventory.user.dto.PermRoleResponseDTO;
import com.bookinventory.user.entity.PermRole;
import com.bookinventory.user.repository.PermRoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PermRoleServiceImpl implements PermRoleService {

    private final PermRoleRepository permRoleRepository;

    // Constructor injection
    public PermRoleServiceImpl(PermRoleRepository permRoleRepository) {
        this.permRoleRepository = permRoleRepository;
    }

    // Convert Entity -> DTO
    private PermRoleResponseDTO convertToDTO(PermRole role) {
        PermRoleResponseDTO dto = new PermRoleResponseDTO();
        dto.setRoleNumber(role.getRoleNumber());
        dto.setPermRole(role.getPermRole());
        return dto;
    }

    // Get All Roles
    @Override
    public List<PermRoleResponseDTO> getAllRoles() {
        List<PermRole> roles = permRoleRepository.findAll();
        List<PermRoleResponseDTO> dtoList = new ArrayList<>();
        for (PermRole role : roles) {
            dtoList.add(convertToDTO(role));
        }
        return dtoList;
    }

    // Get Role By ID
    @Override
    public PermRoleResponseDTO getRoleById(Integer roleNumber) {
        PermRole role = permRoleRepository.findById(roleNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role", "roleNumber", roleNumber));
        return convertToDTO(role);
    }

    // Get Role By Name
    @Override
    public PermRoleResponseDTO getRoleByName(String permRole) {
        PermRole role = permRoleRepository.findByPermRole(permRole)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role", "permRole", permRole));
        return convertToDTO(role);
    }
}