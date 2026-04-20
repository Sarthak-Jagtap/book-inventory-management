package com.bookinventory.user.dto;

import java.util.Map;

public class AdminDashboardDTO {

	private long totalUsers;
	private long totalPurchases;

	// Key = role name (e.g. "Admin"), Value = count of users in that role
	private Map<String, Long> userCountByRole;

	public AdminDashboardDTO() {
	}

	public long getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(long v) {
		this.totalUsers = v;
	}

	public long getTotalPurchases() {
		return totalPurchases;
	}

	public void setTotalPurchases(long v) {
		this.totalPurchases = v;
	}

	public Map<String, Long> getUserCountByRole() {
		return userCountByRole;
	}

	public void setUserCountByRole(Map<String, Long> v) {
		this.userCountByRole = v;
	}
}