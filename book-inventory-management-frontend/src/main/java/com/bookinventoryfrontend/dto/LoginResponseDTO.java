package com.bookinventoryfrontend.dto;

public class LoginResponseDTO {
	private Integer userId;
	private String userName;
	private String firstName;
	private String lastName;
	private String roleName;
	private String message;
	private String token;
	private String tokenType;

	public LoginResponseDTO() {
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer v) {
		this.userId = v;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String v) {
		this.userName = v;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String v) {
		this.firstName = v;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String v) {
		this.lastName = v;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String v) {
		this.roleName = v;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String v) {
		this.message = v;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String v) {
		this.token = v;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String v) {
		this.tokenType = v;
	}
}
