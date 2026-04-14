package com.bookinventory.bookreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.bookreview.entity.BookReview;
import com.bookinventory.bookreview.entity.BookReviewId;

public interface BookReviewRepository extends JpaRepository<BookReview,BookReviewId> {
	
	List<BookReview> findByBookIsbn(String isbn);
	
	List<BookReview> findByBookIsbnAndReviewerReviewerID(String isbn, int reviewerid);
	List<BookReview> findByReviewerReviewerID(int reviewerid);

}
