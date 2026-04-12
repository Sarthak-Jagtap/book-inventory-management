package com.bookinventory.state.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "state")
public class State {

    @Id
    @NotBlank
    @Size(max = 2)
    @Column(name = "StateCode", columnDefinition = "CHAR(2)", nullable = false)
    private String stateCode;

    @Size(max = 50)
    @Column(name = "StateName", length = 50)
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
