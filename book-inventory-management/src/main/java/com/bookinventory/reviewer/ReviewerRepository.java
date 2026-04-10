package com.bookinventory.reviewer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewerRepository extends JpaRepository<Reviewer,Integer> {
	
	List<Reviewer> findByReviewerID(int ReviewerID);

}
