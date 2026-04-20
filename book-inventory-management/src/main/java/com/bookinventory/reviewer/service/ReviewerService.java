package com.bookinventory.reviewer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.reviewer.dto.ReviewerDTO;
import com.bookinventory.reviewer.entity.Reviewer;
import com.bookinventory.reviewer.repository.ReviewerRepository;
import com.bookinventory.common.exception.ResourceNotFoundException;

@Service
public class ReviewerService {
	
	@Autowired
	private ReviewerRepository repo;
	
	public List<Reviewer> getReviewerByReviewerID(int reviewerID){
		return repo.findByReviewerID(reviewerID);	
		
	}
	public List<ReviewerDTO> getReviewerByReviewerIDDTO(int reviewerID){
		List<Reviewer> reviewers= repo.findByReviewerID(reviewerID);
		
		if(reviewers.isEmpty()){
	        throw new ResourceNotFoundException("Reviewer", "reviewerID", reviewerID);
	    }
		
		return reviewers.stream().map(reviewer->{
			ReviewerDTO dto = new ReviewerDTO();
			dto.setReviewerID(reviewer.getReviewerID());
			dto.setName(reviewer.getName());
			dto.setEmployedBy(reviewer.getEmployedBy());
			return dto;
		}).toList();
	}
	
	public List<ReviewerDTO> getReviewerByName(String name){

	    List<Reviewer> reviewers = repo.findByName(name);

	    if(reviewers.isEmpty()){
	        throw new ResourceNotFoundException("Reviewer", "name", name);
	    }

	    return reviewers.stream().map(r ->
	        new ReviewerDTO(r.getReviewerID(), r.getName(), r.getEmployedBy())
	    ).toList();
	}
	
	public List<ReviewerDTO> getReviewerByCompany(String employedBy){

	    List<Reviewer> reviewers = repo.findByEmployedBy(employedBy);

	    if(reviewers.isEmpty()){
	        throw new ResourceNotFoundException("Reviewer", "employedBy", employedBy);
	    }

	    return reviewers.stream().map(r ->
	        new ReviewerDTO(r.getReviewerID(), r.getName(), r.getEmployedBy())
	    ).toList();
	}
	
	public List<ReviewerDTO> getAllReviewers(){

	    List<Reviewer> reviewers = repo.findAll();

	    return reviewers.stream()
	            .map(reviewer -> new ReviewerDTO(
	                    reviewer.getReviewerID(),
	                    reviewer.getName(),
	                    reviewer.getEmployedBy()
	            ))
	            .toList();
	}
	
	public ReviewerDTO createReviewer(ReviewerDTO dto) {

	    // DTO → ENTITY
	    Reviewer reviewer = new Reviewer();
	    reviewer.setReviewerID(dto.getReviewerID());
	    reviewer.setName(dto.getName());
	    reviewer.setEmployedBy(dto.getEmployedBy());

	    // SAVE
	    Reviewer saved = repo.save(reviewer);

	    // ENTITY → DTO
	    ReviewerDTO response = new ReviewerDTO();
	    response.setReviewerID(saved.getReviewerID());
	    response.setName(saved.getName());
	    response.setEmployedBy(saved.getEmployedBy());

	    return response;
	}
	
	public ReviewerDTO updateReviewer(ReviewerDTO dto) {

	    Reviewer reviewer = new Reviewer();
	    reviewer.setReviewerID(dto.getReviewerID());
	    reviewer.setName(dto.getName());
	    reviewer.setEmployedBy(dto.getEmployedBy());

	    Reviewer updated = repo.save(reviewer);

	    ReviewerDTO response = new ReviewerDTO();
	    response.setReviewerID(updated.getReviewerID());
	    response.setName(updated.getName());
	    response.setEmployedBy(updated.getEmployedBy());

	    return response;
	}
	
	public void deleteReviewer(int reviewerID) {
		repo.deleteById(reviewerID);
	}
}
