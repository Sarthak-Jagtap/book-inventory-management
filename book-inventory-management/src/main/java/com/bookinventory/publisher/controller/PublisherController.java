package com.bookinventory.publisher.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bookinventory.publisher.dto.PublisherRequestDTO;
import com.bookinventory.publisher.dto.PublisherResponseDTO;
import com.bookinventory.publisher.service.PublisherService;

import jakarta.validation.Valid;

@RestController
@Validated
public class PublisherController {

	@Autowired
	private PublisherService publisherService;
	
	@PostMapping("/admin/publishers")
    public PublisherResponseDTO createPublisher(@Valid @RequestBody PublisherRequestDTO dto) {
        return publisherService.createPublisher(dto);
    }
	
	@GetMapping("/publishers")
    public List<PublisherResponseDTO> getAllPublishers() {
        return publisherService.getAllPublishers();
    }
	
	@GetMapping("/publishers/{id}")
    public PublisherResponseDTO getPublisherById(@PathVariable int id) {
        return publisherService.getPublisherById(id);
    }
	
	@PutMapping("/admin/publishers/{id}")
    public PublisherResponseDTO updatePublisher(@PathVariable int id,
                                                @Valid @RequestBody PublisherRequestDTO dto) {
        return publisherService.updatePublisher(id, dto);
    }
	
	@DeleteMapping("/admin/publishers/{id}")
    public String deletePublisher(@PathVariable int id) {
        publisherService.deletePublisher(id);
        return "Publisher deleted successfully";
    }
}
