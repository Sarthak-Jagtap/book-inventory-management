package com.bookinventory.user.dto;

import jakarta.validation.constraints.NotNull;

public class RoleUpdateRequestDTO {

	@NotNull(message = "Role number is required")
	private Integer roleNumber;

	public RoleUpdateRequestDTO() {
	}

	public RoleUpdateRequestDTO(Integer roleNumber) {
		this.roleNumber = roleNumber;
	}

	public Integer getRoleNumber() {
		return roleNumber;
	}

	public void setRoleNumber(Integer v) {
		this.roleNumber = v;
	}
}