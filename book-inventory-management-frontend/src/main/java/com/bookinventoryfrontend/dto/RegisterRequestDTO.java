package com.bookinventoryfrontend.dto;

public class RegisterRequestDTO {
	private String lastName;
	private String firstName;
	private String phoneNumber;
	private String userName;
	private String password;
	private Integer roleNumber;

	public RegisterRequestDTO() {
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String v) {
		this.password = v;
	}

	public Integer getRoleNumber() {
		return roleNumber;
	}

	public void setRoleNumber(Integer v) {
		this.roleNumber = v;
	}
}