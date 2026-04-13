package com.bookinventory.state.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.publisher.dto.PublisherResponseDTO;
import com.bookinventory.publisher.entity.Publisher;
import com.bookinventory.publisher.repository.PublisherRepository;
import com.bookinventory.publisher.service.PublisherService;
import com.bookinventory.state.dto.StateRequestDTO;
import com.bookinventory.state.dto.StateResponseDTO;
import com.bookinventory.state.dto.StateWithPublishersDTO;
import com.bookinventory.state.entity.State;
import com.bookinventory.state.repository.StateRepository;
import com.bookinventory.user.common.exception.ResourceNotFoundException;

@Service
public class StateService {

	@Autowired
	private PublisherService publisherService;
	
	@Autowired
	private PublisherRepository publisherRepository;
	
	@Autowired
	private StateRepository stateRepository;

	public StateResponseDTO createState(StateRequestDTO dto) {

		State state = convertToEntity(dto);

		State savedState = stateRepository.save(state);

		return convertToDTO(savedState);
	}

	public List<StateResponseDTO> getAllStates() {

		return stateRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public StateResponseDTO getStateByCode(String code) {

		State state = stateRepository.findById(code)
				.orElseThrow(() -> new ResourceNotFoundException("State", "code", code));

		return convertToDTO(state);
	}

	public StateResponseDTO updateState(String code, StateRequestDTO dto) {

		State existingState = stateRepository.findById(code)
				.orElseThrow(() -> new ResourceNotFoundException("State", "code", code));

		existingState.setStateName(dto.getStateName());

		State updatedState = stateRepository.save(existingState);

		return convertToDTO(updatedState);
	}

	public void deleteState(String code) {

		State state = stateRepository.findById(code)
				.orElseThrow(() -> new ResourceNotFoundException("State", "code", code));

		stateRepository.delete(state);
	}

	public StateWithPublishersDTO getStateWithPublishers(String stateCode) {

	    State state = stateRepository.findById(stateCode)
	            .orElseThrow(() -> new ResourceNotFoundException("State", "code", stateCode));

	    List<Publisher> publishers = publisherRepository.findByState_StateCode(stateCode);

	    List<PublisherResponseDTO> publisherDTOs = publishers.stream()
	            .map(publisherService::convertToDTO)
	            .toList();

	    StateWithPublishersDTO dto = new StateWithPublishersDTO();
	    dto.setStateCode(state.getStateCode());
	    dto.setStateName(state.getStateName());
	    dto.setPublishers(publisherDTOs);

	    return dto;
	}
	
	private State convertToEntity(StateRequestDTO dto) {

		State state = new State();
		state.setStateCode(dto.getStateCode());
		state.setStateName(dto.getStateName());

		return state;
	}

	private StateResponseDTO convertToDTO(State state) {

		StateResponseDTO dto = new StateResponseDTO();
		dto.setStateCode(state.getStateCode());
		dto.setStateName(state.getStateName());

		return dto;
	}
}