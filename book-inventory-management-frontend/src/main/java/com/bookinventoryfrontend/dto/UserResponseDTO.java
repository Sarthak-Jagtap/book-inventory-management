package com.bookinventoryfrontend.dto;

public class UserResponseDTO {
	private Integer userId;
	private String lastName;
	private String firstName;
	private String phoneNumber;
	private String userName;
	private PermRoleResponseDTO role;

	public UserResponseDTO() {
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer v) {
		this.userId = v;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String v) {
		this.lastName = v;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String v) {
		this.firstName = v;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String v) {
		this.phoneNumber = v;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String v) {
		this.userName = v;
	}

	public PermRoleResponseDTO getRole() {
		return role;
	}

	public void setRole(PermRoleResponseDTO v) {
		this.role = v;
	}

	// Convenience: get full name
	public String getFullName() {
		return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
	}

	
	public String getInitials() {
		String f = (firstName != null && !firstName.isEmpty()) ? firstName.substring(0, 1) : "";
		String l = (lastName != null && !lastName.isEmpty()) ? lastName.substring(0, 1) : "";
		return (f + l).toUpperCase();
	}
}
