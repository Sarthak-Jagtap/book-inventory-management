package com.bookinventory.reviewer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewerController {
	
	@Autowired
	private ReviewerService service;
	
	@GetMapping("/reviewer")
	public List<ReviewerDTO> getReviewer(@RequestParam int reviewerId,Model model) {
		
		return service.getReviewerByReviewerIDDTO(reviewerId);
		
	}

}
