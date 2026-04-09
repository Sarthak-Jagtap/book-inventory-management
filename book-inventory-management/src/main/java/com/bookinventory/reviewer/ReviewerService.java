package com.bookinventory.reviewer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewerService {
	
	@Autowired
	private ReviewerRepository repo;
	
	List<Reviewer> getReviewerByReviewerID(int ReviewerID){
		return repo.findByReviewerID(ReviewerID);
	}
	
	List<ReviewerDTO> getReviewerByReviewerIDDTO(int ReviewerID){
		List<Reviewer> reviewers= repo.findByReviewerID(ReviewerID);
		
		return reviewers.stream().map(reviewer->{
			ReviewerDTO dto = new ReviewerDTO();
			dto.setName(reviewer.getName());
			dto.setEmployedBy(reviewer.getEmployedBy());
			return dto;
		}).toList();
	}
}
