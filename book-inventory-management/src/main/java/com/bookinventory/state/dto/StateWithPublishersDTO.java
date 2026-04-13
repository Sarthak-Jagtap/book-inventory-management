package com.bookinventory.state.dto;

import java.util.List;

import com.bookinventory.publisher.dto.PublisherResponseDTO;

public class StateWithPublishersDTO {

	private String stateCode;
	private String stateName;
	private List<PublisherResponseDTO> publishers;

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

	public List<PublisherResponseDTO> getPublishers() {
		return publishers;
	}

	public void setPublishers(List<PublisherResponseDTO> publishers) {
		this.publishers = publishers;
	}

}
