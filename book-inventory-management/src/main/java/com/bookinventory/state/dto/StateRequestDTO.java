package com.bookinventory.state.dto;

import jakarta.validation.constraints.NotBlank;

public class StateRequestDTO {

	@NotBlank(message = "State code is required")
	private String stateCode;
	
	@NotBlank(message = "State name is required")
	private String stateName;

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

}
