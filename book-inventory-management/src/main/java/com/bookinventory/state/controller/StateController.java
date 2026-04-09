package com.bookinventory.state.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookinventory.state.entity.State;
import com.bookinventory.state.service.StateService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/state")
public class StateController {

	@Autowired
	private StateService service;
	
	@GetMapping
	public List<State> getAllStates() {
		return service.getAllStates();
	}
	
}
