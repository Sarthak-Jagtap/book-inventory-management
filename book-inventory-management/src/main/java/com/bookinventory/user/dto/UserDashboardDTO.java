package com.bookinventory.user.dto;

import java.util.List;

public class UserDashboardDTO {

	// Profile info
	private Integer userId;
	private String firstName;
	private String lastName;
	private String userName;
	private String phoneNumber;
	private String roleName;

	// Purchase summary
	private long totalPurchases;
	private List<Integer> purchasedInventoryIds;

	public UserDashboardDTO() {
	}

	// Getters & Setters
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer v) {
		this.userId = v;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String v) {
		this.userName = v;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String v) {
		this.phoneNumber = v;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String v) {
		this.roleName = v;
	}

	public long getTotalPurchases() {
		return totalPurchases;
	}

	public void setTotalPurchases(long v) {
		this.totalPurchases = v;
	}

	public List<Integer> getPurchasedInventoryIds() {
		return purchasedInventoryIds;
	}

	public void setPurchasedInventoryIds(List<Integer> v) {
		this.purchasedInventoryIds = v;
	}
}