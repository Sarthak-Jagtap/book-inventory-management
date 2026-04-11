package com.bookinventory.bookreview;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookReviewService {
	
	@Autowired
	private BookReviewRepository repo;
	
	List<BookReview> getBookReviewByISBN(String isbn){
		return repo.findByBookIsbn(isbn);
		
	}
	
	List<BookReviewDTO> getBookReviewByISBNDTO(String isbn){
		List<BookReview> reviews=repo.findByBookIsbn(isbn);
		
		return reviews.stream().map(review-> {
			BookReviewDTO dto = new BookReviewDTO();
			dto.setRating(review.getRating());
			dto.setComments(review.getComments());
			return dto;
		}).toList();
	}
	
	
	

}
