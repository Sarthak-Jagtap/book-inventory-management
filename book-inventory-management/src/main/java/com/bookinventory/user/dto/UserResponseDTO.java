package com.bookinventory.user.dto;

public class UserResponseDTO {

	private Integer userId;
	private String lastName;
	private String firstName;
	private String phoneNumber;
	private String userName;
	private boolean active;
	private PermRoleResponseDTO role;

	public UserResponseDTO() {
	}

	// Getters & Setters
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public PermRoleResponseDTO getRole() {
		return role;
	}

	public void setRole(PermRoleResponseDTO role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserResponseDTO{userId=" + userId + ", userName='" + userName + "', active=" + active + ", role=" + role
				+ '}';
	}
}