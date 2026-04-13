package com.bookinventory.publisher.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.publisher.dto.PublisherRequestDTO;
import com.bookinventory.publisher.dto.PublisherResponseDTO;
import com.bookinventory.publisher.entity.Publisher;
import com.bookinventory.publisher.repository.PublisherRepository;
import com.bookinventory.state.entity.State;
import com.bookinventory.state.repository.StateRepository;
import com.bookinventory.user.common.exception.ResourceNotFoundException;

@Service
public class PublisherService {

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private StateRepository stateRepository;

	public PublisherResponseDTO createPublisher(PublisherRequestDTO dto) {

		State state = stateRepository.findById(dto.getStateCode())
				.orElseThrow(() -> new ResourceNotFoundException("State", "code", dto.getStateCode()));

		Publisher publisher = convertToEntity(dto, state);

		Publisher savedPublisher = publisherRepository.save(publisher);

		return convertToDTO(savedPublisher);
	}

	public List<PublisherResponseDTO> getAllPublishers() {

		return publisherRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public PublisherResponseDTO getPublisherById(int id) {

		Publisher publisher = publisherRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Publisher", "id", id));

		return convertToDTO(publisher);
	}

	public PublisherResponseDTO updatePublisher(int id, PublisherRequestDTO dto) {

		Publisher existingPublisher = publisherRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Publisher", "id", id));

		State state = stateRepository.findById(dto.getStateCode())
				.orElseThrow(() -> new ResourceNotFoundException("State", "code", dto.getStateCode()));

		existingPublisher.setName(dto.getName());
		existingPublisher.setCity(dto.getCity());
		existingPublisher.setState(state);

		Publisher updatedPublisher = publisherRepository.save(existingPublisher);

		return convertToDTO(updatedPublisher);
	}

	public void deletePublisher(int id) {

		Publisher publisher = publisherRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Publisher", "id", id));

		publisherRepository.delete(publisher);
	}
	
	public List<PublisherResponseDTO> getPublishersByState(String stateCode) {

	    List<Publisher> publishers = publisherRepository.findByState_StateCode(stateCode);

	    return publishers.stream()
	            .map(this::convertToDTO)
	            .toList();
	}
	

	private Publisher convertToEntity(PublisherRequestDTO dto, State state) {
		Publisher publisher = new Publisher();
		publisher.setName(dto.getName());
		publisher.setCity(dto.getCity());
		publisher.setState(state);
		return publisher;
	}

	private PublisherResponseDTO convertToDTO(Publisher publisher) {
		PublisherResponseDTO dto = new PublisherResponseDTO();
		dto.setPublisherId(publisher.getPublisherId()); // adjust based on your entity naming
		dto.setName(publisher.getName());
		dto.setCity(publisher.getCity());
		dto.setStateCode(publisher.getState().getStateCode());
		return dto;
	}
}
