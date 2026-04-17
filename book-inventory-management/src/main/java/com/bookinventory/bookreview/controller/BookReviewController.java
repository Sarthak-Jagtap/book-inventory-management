package com.bookinventory.bookreview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bookinventory.bookreview.dto.BookReviewDTO;
import com.bookinventory.bookreview.entity.BookReview;
import com.bookinventory.bookreview.service.BookReviewService;
import com.bookinventory.user.common.response.ApiResponse;

@RestController
@RequestMapping("/api/v1")
public class BookReviewController {
    
    @Autowired
    private BookReviewService service;

    //  GET reviews by ISBN
    @GetMapping("/reviews/book/{isbn}")
    public List<BookReviewDTO> getReviewsByIsbn(@PathVariable String isbn) {
        return service.getBookReviewByISBNDTOisbn(isbn);
    }
    // GET count of book reviews
    @GetMapping("/reviews/book/{isbn}/count")
    public ResponseEntity<ApiResponse<String>> getReviewsCount(@PathVariable String isbn) {
        int reviewCount=service.getBookReviewByISBNDTOisbn(isbn).size();
        
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Total reviews for ISBN: "+isbn+" = " +reviewCount));
    }
    
    // GET reviews given by particular reviewer
    @GetMapping("/reviews/reviewer/{reviewerId}")
    public List<BookReviewDTO> getReviewsByReviewerid(@PathVariable int reviewerId) {
        return service.getBookReviewByISBNDTOreviewer(reviewerId);
    }
    
    // GET count of book reviews
    @GetMapping("/reviews/reviewer/{reviewerId}/count")
    public ResponseEntity<ApiResponse<String>> getReviewerReviewsCount(@PathVariable int reviewerId) {
        int reviewCount=service.getBookReviewByISBNDTOreviewer(reviewerId).size();
        
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Total reviews for Reviewer: "+reviewerId+" = " +reviewCount));
    }
    
    //  GET reviews by ISBN and reviewerID
    @GetMapping("/reviews/{reviewerId}/book/{isbn}")
    public List<BookReviewDTO> getParticulerReviews(@PathVariable String isbn ,@PathVariable int reviewerId) {
        return service.getBookReviewByISBNDTO(isbn,reviewerId);
    }

    //  GET all reviews
    @GetMapping("reviews/book")
    public List<BookReviewDTO> getAllReviewsDTO(){
        return service.getAllReviewsDTO();
    }

    //  CREATE review
    @PostMapping("/reviews/book")
    public BookReview createReview(@RequestBody BookReview review) {
        return service.createReview(review);
    }

    //  UPDATE review
    @PutMapping("/reviews/book")
    public BookReview updateReview(@RequestBody BookReview review) {
        return service.updateReview(review);
    }

    //  DELETE review 
    @DeleteMapping("/reviews/book/{isbn}/{reviewerId}")
    public String deleteReview(@PathVariable String isbn,
                               @PathVariable Integer reviewerId) {
        
        service.deleteReview(isbn, reviewerId);
        return "Review Deleted Successfully";
    }
}