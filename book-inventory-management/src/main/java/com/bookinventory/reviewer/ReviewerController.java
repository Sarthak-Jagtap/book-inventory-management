package com.bookinventory.reviewer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReviewerController {
	
	@Autowired
	private ReviewerService service;
	
	@GetMapping("/reviwer")
	public String getReviewer(@RequestParam int ReviewerId,Model model) {
		
		List<ReviewerDTO> reviewers=service.getReviewerByReviewerIDDTO(ReviewerId);
		
		model.addAttribute("reviewers",reviewers);
		
		return "review-list";
		
	}

}
