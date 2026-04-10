package com.bookinventory.bookreview;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReviewRepository extends JpaRepository<BookReview,BookReviewId> {
	
	List<BookReview> findByBookIsbn(String isbn);

}
