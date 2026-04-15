package com.bookinventory.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UserID")
	private Integer userId;

	@Column(name = "LastName", length = 30, nullable = false)
	private String lastName;

	@Column(name = "FirstName", length = 20, nullable = false)
	private String firstName;

	@Column(name = "PhoneNumber", length = 14, columnDefinition = "CHAR(14)")
	private String phoneNumber;

	@Column(name = "UserName", length = 30, nullable = false)
	private String userName;

	@Column(name = "Password", length = 30, nullable = false)
	private String password;

	// NEW: soft-delete flag — true = active user, false = deactivated
	@Column(name = "active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
	private boolean active = true;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RoleNumber", referencedColumnName = "RoleNumber")
	private PermRole role;

	// Constructors
	public User() {
	}

	public User(String lastName, String firstName, String phoneNumber, String userName, String password,
			PermRole role) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.phoneNumber = phoneNumber;
		this.userName = userName;
		this.password = password;
		this.role = role;
		this.active = true; // always start as active
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String v) {
		this.password = v;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public PermRole getRole() {
		return role;
	}

	public void setRole(PermRole role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User{userId=" + userId + ", userName='" + userName + "', active=" + active + ", role="
				+ (role != null ? role.getPermRole() : "null") + '}';
	}
}