package com.bookinventory.publisher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.publisher.entity.Publisher;
import com.bookinventory.publisher.repository.PublisherRepository;
import com.bookinventory.state.entity.State;
import com.bookinventory.state.repository.StateRepository;

@Service
public class PublisherService {

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private StateRepository stateRepository;

	public Publisher addPublisher(Publisher publisher, String stateCode) {
		State state = stateRepository.findById(stateCode).orElseThrow(() -> new RuntimeException("State not Found"));

		publisher.setState(state);

		return publisherRepository.save(publisher);
	}

	public List<Publisher> getAllPublishers() {
		return publisherRepository.findAll();
	}

	public Publisher getPublisherById(int id) {
		return publisherRepository.findById(id).orElseThrow(() -> new RuntimeException("Publisher Not Found"));
	}

	public Publisher updatePublisherById(int id, Publisher updatedPublisher, String stateCode) {

		Publisher existingPublisher = publisherRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Publisher not found"));

		State state = stateRepository.findById(stateCode).orElseThrow(() -> new RuntimeException("State not found"));
		
		existingPublisher.setName(updatedPublisher.getName());
		existingPublisher.setCity(updatedPublisher.getCity());
		
		existingPublisher.setState(state);
		
		return publisherRepository.save(existingPublisher);
	}
	
	public void deletePublisherById(int id) {
		// Exception Handling Required
		publisherRepository.deleteById(id);
	}
}
