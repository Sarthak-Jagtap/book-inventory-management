package com.bookinventory.reviewer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookinventory.reviewer.dto.ReviewerDTO;
import com.bookinventory.reviewer.service.ReviewerService;
import com.bookinventory.user.common.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ReviewerController {
	
	@Autowired
	private ReviewerService service;
	
	@GetMapping("/reviewer/{reviewerID}")
	public List<ReviewerDTO> getReviewer(@PathVariable int reviewerID,Model model) {
		
		return service.getReviewerByReviewerIDDTO(reviewerID);
		
	}
	
	
	@GetMapping(value = "/reviewer", params = "name")
	public List<ReviewerDTO> getReviewerByName(@RequestParam String name) {
	    return service.getReviewerByName(name);
	}
	
	
	@GetMapping(value = "/reviewer", params = "employedBy")
	public List<ReviewerDTO> getReviewerByCompany(@RequestParam String employedBy) {
	    return service.getReviewerByCompany(employedBy);
	}
	
	@GetMapping("/reviewer/count")
    public ResponseEntity<ApiResponse<String>> getReviewsCount(@RequestParam String employedBy) {
        int reviewerCount= service.getReviewerByCompany(employedBy).size();
        
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Total reviewers employed by : "+employedBy+" = " +reviewerCount));
    }
	
	@GetMapping("/reviewer")
	public List<ReviewerDTO> getAllReviewer(){
		return service.getAllReviewers();
	}
	
	@PostMapping("/reviewer")
	public ReviewerDTO createReviewer(@Valid @RequestBody ReviewerDTO reviewerdto) {
		return service.createReviewer(reviewerdto);
	}
	
	@PutMapping("/reviewer")
	public ReviewerDTO updateReviewer(@Valid @RequestBody ReviewerDTO reviewerdto) {
		return service.updateReviewer(reviewerdto);
	}
	
	@DeleteMapping("/reviewer/{reviewerID}")
	public String deleteReviewer(@PathVariable int reviewerID) {
		service.deleteReviewer(reviewerID);
		return "Reviewer deleted Succesfully"; 
	}

}
