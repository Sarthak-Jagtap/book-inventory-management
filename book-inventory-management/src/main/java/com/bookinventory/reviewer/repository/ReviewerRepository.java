package com.bookinventory.reviewer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.reviewer.entity.Reviewer;

public interface ReviewerRepository extends JpaRepository<Reviewer,Integer> {
	
	List<Reviewer> findByReviewerID(int reviewerID);
	
	List<Reviewer> findByName(String name);

    List<Reviewer> findByEmployedBy(String employedBy);

}
