package com.bookinventory.publisher.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.publisher.dto.PublisherRequestDTO;
import com.bookinventory.publisher.dto.PublisherResponseDTO;
import com.bookinventory.publisher.entity.Publisher;
import com.bookinventory.publisher.repository.PublisherRepository;
import com.bookinventory.state.entity.State;
import com.bookinventory.state.repository.StateRepository;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

	@Mock
	private PublisherRepository publisherRepository;

	@Mock
	private StateRepository stateRepository;

	@InjectMocks
	private PublisherService publisherService;

	private Publisher publisher;
	private State state;
	private PublisherRequestDTO requestDTO;

	@BeforeEach
	void setUp() {
		state = new State();
		state.setStateCode("MH");
		state.setStateName("Maharashtra");

		publisher = new Publisher();
		publisher.setPublisherId(1);
		publisher.setName("ABC Publications");
		publisher.setCity("Pune");
		publisher.setState(state);

		requestDTO = new PublisherRequestDTO();
		requestDTO.setName("ABC Publications");
		requestDTO.setCity("Pune");
		requestDTO.setStateCode("MH");
	}

	// Create Publisher : Success
	@Test
	void testCreatePublisher_Success() {
		when(stateRepository.findById("MH")).thenReturn(Optional.of(state));
		when(publisherRepository.save(any(Publisher.class))).thenReturn(publisher);

		PublisherResponseDTO reponseDTO = publisherService.createPublisher(requestDTO);

		assertNotNull(reponseDTO);
		assertEquals("ABC Publications", reponseDTO.getName());
		assertEquals("MH", reponseDTO.getStateCode());

		verify(stateRepository).findById("MH");
		verify(publisherRepository).save(any(Publisher.class));
	}

	// Create Publisher : State Not Found

	@Test
	void testCreatePublisher_StateNotFound() {

		when(stateRepository.findById("MH")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			publisherService.createPublisher(requestDTO);
		});

		verify(stateRepository).findById("MH");
		verify(publisherRepository, never()).save(any());
	}

	// Get All Publishers : Success
	@Test
	void testGetAllPublishers() {

		when(publisherRepository.findAll()).thenReturn(List.of(publisher));

		List<PublisherResponseDTO> result = publisherService.getAllPublishers();

		assertEquals(1, result.size());
		assertEquals("ABC Publications", result.get(0).getName());

		verify(publisherRepository).findAll();
	}

	// Get Publisher By Id : Success
	@Test
	void testGetPublisherById_Success() {

		when(publisherRepository.findById(1)).thenReturn(Optional.of(publisher));

		PublisherResponseDTO result = publisherService.getPublisherById(1);

		assertNotNull(result);
		assertEquals("ABC Publications", result.getName());

		verify(publisherRepository).findById(1);
	}

	// Get Publisher By Id : Not Found
	@Test
	void testGetPublisherById_NotFound() {

		when(publisherRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			publisherService.getPublisherById(1);
		});

		verify(publisherRepository).findById(1);
	}

	// Update Publisher Data : Success
	@Test
	void testUpdatePublisher_Success() {

		PublisherRequestDTO updatedDTO = new PublisherRequestDTO();
		updatedDTO.setName("XYZ Publications");
		updatedDTO.setCity("Mumbai");
		updatedDTO.setStateCode("MH");

		when(publisherRepository.findById(1)).thenReturn(Optional.of(publisher));

		when(stateRepository.findById("MH")).thenReturn(Optional.of(state));

		when(publisherRepository.save(any(Publisher.class))).thenReturn(publisher);

		PublisherResponseDTO result = publisherService.updatePublisher(1, updatedDTO);

		assertEquals("XYZ Publications", result.getName());

		verify(publisherRepository).findById(1);
		verify(stateRepository).findById("MH");
		verify(publisherRepository).save(publisher);
	}

	// Delete Publisher : Success
	@Test
	void testDeletePublisher_Success() {

		when(publisherRepository.findById(1)).thenReturn(Optional.of(publisher));

		doNothing().when(publisherRepository).delete(publisher);

		publisherService.deletePublisher(1);

		verify(publisherRepository).findById(1);
		verify(publisherRepository).delete(publisher);
	}

	// Get Publisher By State
	@Test
	void testGetPublishersByState() {

		when(publisherRepository.findByState_StateCode("MH")).thenReturn(List.of(publisher));

		List<PublisherResponseDTO> result = publisherService.getPublishersByState("MH");

		assertEquals(1, result.size());
		assertEquals("ABC Publications", result.get(0).getName());

		verify(publisherRepository).findByState_StateCode("MH");
	}
}
