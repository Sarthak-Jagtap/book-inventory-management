package com.bookinventory.state.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.state.dto.StateResponseDTO;
import com.bookinventory.state.service.StateService;
import com.bookinventory.user.util.JwtUtil;

@WebMvcTest(controllers = StateController.class)
@AutoConfigureMockMvc(addFilters = false)
class StateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StateService stateService;

	@MockBean
	private JwtUtil jwtUtil;

	// GET /api/v1/states/MH
	@Test
	void testGetStateByCode_success() throws Exception {
		StateResponseDTO response = new StateResponseDTO();
		response.setStateCode("MH");
		response.setStateName("Maharashtra");

		when(stateService.getStateByCode("MH")).thenReturn(response);

		mockMvc.perform(get("/api/v1/states/MH")).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.stateCode").value("MH"))
				.andExpect(jsonPath("$.data.stateName").value("Maharashtra")).andDo(print());
	}

	// GET /api/v1/states/MH
	@Test
	void testGetStateByCode_notFound() throws Exception {

	    when(stateService.getStateByCode("MP"))
	            .thenThrow(new ResourceNotFoundException("State", "code", "MP"));

	    mockMvc.perform(get("/api/v1/states/MP"))
	            .andExpect(status().isNotFound())
	            .andDo(print());
	}

	// GET /api/v1/states
	@Test
	void testGetAllStates() throws Exception {

		StateResponseDTO response = new StateResponseDTO();
		response.setStateCode("MH");

		when(stateService.getAllStates()).thenReturn(List.of(response));

		mockMvc.perform(get("/api/v1/states")).andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].stateCode").value("MH"));
	}

}