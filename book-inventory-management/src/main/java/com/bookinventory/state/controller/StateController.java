package com.bookinventory.state.controller;

import com.bookinventory.state.dto.StateResponseDTO;
import com.bookinventory.state.service.StateService;
import com.bookinventory.user.common.response.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StateController {

	private final StateService stateService;

	public StateController(StateService stateService) {
		this.stateService = stateService;
	}

	@GetMapping("/states")
	public ResponseEntity<ApiResponse<List<StateResponseDTO>>> getAllStates() {
		return ResponseEntity.ok(ApiResponse.success(200, "States fetched successfully", stateService.getAllStates()));
	}

	@GetMapping("/states/{stateCode}")
	public ResponseEntity<ApiResponse<StateResponseDTO>> getStateByCode(@PathVariable String stateCode) {

		return ResponseEntity
				.ok(ApiResponse.success(200, "State fetched successfully", stateService.getStateByCode(stateCode)));
	}
}