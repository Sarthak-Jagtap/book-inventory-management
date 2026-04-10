package com.bookinventory.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "permrole")
public class PermRole {

    @Id
    @Column(name = "RoleNumber")
    private Integer roleNumber;

    @Column(name = "PermRole", length = 30)
    private String permRole;

    // Constructors
    public PermRole() {}

    public PermRole(Integer roleNumber, String permRole) {
        this.roleNumber = roleNumber;
        this.permRole = permRole;
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

    // toString
    @Override
    public String toString() {
        return "PermRole{" +
                "roleNumber=" + roleNumber +
                ", permRole='" + permRole + '\'' +
                '}';
    }
}