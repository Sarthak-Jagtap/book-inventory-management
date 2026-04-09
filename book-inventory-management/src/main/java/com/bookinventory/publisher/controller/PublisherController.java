package com.bookinventory.publisher.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookinventory.publisher.entity.Publisher;
import com.bookinventory.publisher.service.PublisherService;

@RestController
@RequestMapping("/publisher")
public class PublisherController {

	@Autowired
	private PublisherService service;
	
	@GetMapping
	public List<Publisher> showAllPublisher(){
		return service.getAllPublishers();
	}
}
