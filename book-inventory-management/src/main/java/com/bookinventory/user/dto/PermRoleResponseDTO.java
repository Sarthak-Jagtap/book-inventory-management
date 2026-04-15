package com.bookinventory.user.dto;

public class PermRoleResponseDTO {

    private Integer roleNumber;
    private String permRole;

    // Constructors
    public PermRoleResponseDTO() {}

    public PermRoleResponseDTO(Integer roleNumber, String permRole) {
        this.roleNumber = roleNumber;
        this.permRole   = permRole;
    }

    // Getters & Setters
    public Integer getRoleNumber() {
        return roleNumber;
    }

    public void setRoleNumber(Integer roleNumber) {
        this.roleNumber = roleNumber;
    }

    public String getPermRole() {
        return permRole;
    }

    public void setPermRole(String permRole) {
        this.permRole = permRole;
    }

    @Override
    public String toString() {
        return "PermRoleResponseDTO{" +
                "roleNumber=" + roleNumber +
                ", permRole='" + permRole + '\'' +
                '}';
    }
}
