package com.bookinventory.user.dto;

import jakarta.validation.constraints.NotNull;

public class UserStatusUpdateDTO {

	@NotNull(message = "active status is required (true/false)")
	private Boolean active;

	public UserStatusUpdateDTO() {
	}

	public UserStatusUpdateDTO(Boolean active) {
		this.active = active;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}