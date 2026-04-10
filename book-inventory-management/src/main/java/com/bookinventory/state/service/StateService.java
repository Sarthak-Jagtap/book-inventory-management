package com.bookinventory.state.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.state.entity.State;
import com.bookinventory.state.repository.StateRepository;

@Service
public class StateService {

	@Autowired
	private StateRepository repository;

	public State addState(State state) {
		return repository.save(state);
	}

	public List<State> getAllStates() {
		return repository.findAll();
	}

	public State getStateById(String stateCode) {
		// Exception Handling Required	
		return repository.findById(stateCode).orElseThrow(() -> new RuntimeException());
	}
	
	public State updateState(String stateCode, String stateName) {
		// Exception Handling Required	
		State state = repository.findById(stateName).orElseThrow(() -> new RuntimeException());
		
		state.setStateName(stateName);
		
		return repository.save(state);
	}
	
	public void deleteState(String stateCode) {
		// Exception Handling Required	
		repository.deleteById(stateCode);
	}
}
