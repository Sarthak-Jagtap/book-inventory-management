package com.bookinventory.reviewer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewerService {
	
	@Autowired
	private ReviewerRepository repo;
	
	List<Reviewer> getReviewerByReviewerID(int reviewerID){
		return repo.findByReviewerID(reviewerID);
	}
	
	List<ReviewerDTO> getReviewerByReviewerIDDTO(int reviewerID){
		List<Reviewer> reviewers= repo.findByReviewerID(reviewerID);
		
		return reviewers.stream().map(reviewer->{
			ReviewerDTO dto = new ReviewerDTO();
			dto.setName(reviewer.getName());
			dto.setEmployedBy(reviewer.getEmployedBy());
			return dto;
		}).toList();
	}
}
