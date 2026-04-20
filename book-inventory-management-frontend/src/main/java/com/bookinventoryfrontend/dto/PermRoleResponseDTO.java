package com.bookinventoryfrontend.dto;

public class PermRoleResponseDTO {
	private Integer roleNumber;
	private String permRole;

	public PermRoleResponseDTO() {
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
}
