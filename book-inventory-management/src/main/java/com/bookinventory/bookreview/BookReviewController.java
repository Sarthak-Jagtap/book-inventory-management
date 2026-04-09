package com.bookinventory.bookreview;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookReviewController {
	
	@Autowired
	private BookReviewService service;
	
	@GetMapping("/reviews/{isbn}")
	public String getReviews(@RequestParam String ISBN,Model model) {
		List<BookReviewDTO> reviews=service.getBookReviewByISBNDTO(ISBN);
		
		model.addAttribute("reviews",reviews);
		
		
		return "review-list";
		
	}

}
