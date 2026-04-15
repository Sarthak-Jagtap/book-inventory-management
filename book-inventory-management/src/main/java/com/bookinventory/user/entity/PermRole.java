package com.bookinventory.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "permrole")
public class PermRole {

	@Id
	@Column(name = "RoleNumber")
	private Integer roleNumber;

	@Column(name = "PermRole", length = 30)
	private String permRole;

	public PermRole() {
	}

	public PermRole(Integer roleNumber, String permRole) {
		this.roleNumber = roleNumber;
		this.permRole = permRole;
	}

	public Integer getRoleNumber() {
		return roleNumber;
	}

	public void setRoleNumber(Integer v) {
		this.roleNumber = v;
	}

	public String getPermRole() {
		return permRole;
	}

	public void setPermRole(String v) {
		this.permRole = v;
	}

	@Override
	public String toString() {
		return "PermRole{roleNumber=" + roleNumber + ", permRole='" + permRole + "'}";
	}
}