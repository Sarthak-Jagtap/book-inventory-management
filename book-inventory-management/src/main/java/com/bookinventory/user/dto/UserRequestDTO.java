package com.bookinventory.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRequestDTO {
	@NotBlank(message = "Last name is required")
	@Size(max = 30, message = "Last name must not exceed 30 characters")
	private String lastName;

	@NotBlank(message = "First name is required")
	@Size(max = 20, message = "First name must not exceed 20 characters")
	private String firstName;

	// Phone format matches DB sample data: (717) 555-0975 → 14 chars
	@Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "Phone number must be in format (XXX) XXX-XXXX")
	private String phoneNumber;

	@NotBlank(message = "Username is required")
	@Size(max = 30, message = "Username must not exceed 30 characters")
	private String userName;

	@NotBlank(message = "Password is required")
	@Size(min = 4, max = 30, message = "Password must be between 4 and 30 characters")
	private String password;

	// roleNumber is optional on registration — DB defaults to 1 (Guest)
	// Admin can assign a different role explicitly
	private Integer roleNumber;

	// Constructors
	public UserRequestDTO() {
	}

	public UserRequestDTO(String lastName, String firstName, String phoneNumber, String userName, String password,
			Integer roleNumber) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.phoneNumber = phoneNumber;
		this.userName = userName;
		this.password = password;
		this.roleNumber = roleNumber;
	}

	// Getters & Setters
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getRoleNumber() {
		return roleNumber;
	}

	public void setRoleNumber(Integer roleNumber) {
		this.roleNumber = roleNumber;
	}

	@Override
	public String toString() {
		return "UserRequestDTO{" + "lastName='" + lastName + '\'' + ", firstName='" + firstName + '\''
				+ ", phoneNumber='" + phoneNumber + '\'' + ", userName='" + userName + '\'' + ", roleNumber="
				+ roleNumber + '}';
	}
}
