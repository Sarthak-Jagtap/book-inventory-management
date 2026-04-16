package com.bookinventory.state.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.bookinventory.state.dto.StateRequestDTO;
import com.bookinventory.state.dto.StateResponseDTO;
import com.bookinventory.state.entity.State;
import com.bookinventory.state.repository.StateRepository;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.publisher.repository.PublisherRepository;
import com.bookinventory.publisher.service.PublisherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StateServiceTest {

	@Mock
	private StateRepository stateRepository;

	@Mock
	private PublisherRepository publisherRepository;

	@Mock
	private PublisherService publisherService;

	@InjectMocks
	private StateService stateService;

	private State state;
	private StateRequestDTO requestDTO;

	@BeforeEach
	void setUp() {

		state = new State();
		state.setStateCode("MH");
		state.setStateName("Maharashtra");

		requestDTO = new StateRequestDTO();
		requestDTO.setStateCode("MH");
		requestDTO.setStateName("Maharashtra");
	}

	// GET ALL STATES
	@Test
	void testGetAllStates() {

		when(stateRepository.findAll()).thenReturn(List.of(state));

		List<StateResponseDTO> result = stateService.getAllStates();

		assertEquals(1, result.size());
		assertEquals("MH", result.get(0).getStateCode());

		verify(stateRepository).findAll();
	}

	// GET STATE BY CODE - SUCCESS
	@Test
	void testGetStateByCode_Success() {

		when(stateRepository.findById("MH")).thenReturn(Optional.of(state));

		StateResponseDTO result = stateService.getStateByCode("MH");

		assertNotNull(result);
		assertEquals("MH", result.getStateCode());

		verify(stateRepository).findById("MH");
	}

	// GET STATE BY CODE - NOT FOUND
	@Test
	void testGetStateByCode_notFound() {

		when(stateRepository.findById("MH")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			stateService.getStateByCode("MH");
		});

		verify(stateRepository).findById("MH");
	}
}