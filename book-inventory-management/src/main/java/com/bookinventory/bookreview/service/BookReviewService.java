package com.bookinventory.bookreview.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.book.entity.Book;
import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.bookreview.dto.BookReviewDTO;
import com.bookinventory.bookreview.entity.BookReview;
import com.bookinventory.bookreview.entity.BookReviewId;
import com.bookinventory.bookreview.repository.BookReviewRepository;
import com.bookinventory.reviewer.entity.Reviewer;
import com.bookinventory.reviewer.repository.ReviewerRepository;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.common.exception.DuplicateResourceException;

@Service
public class BookReviewService {
    
    @Autowired
    private BookReviewRepository repo;

    @Autowired
    private ReviewerRepository reviewerRepo;

    @Autowired
    private BookRepository bookRepo;

   
    public List<BookReview> getBookReviewByISBN(String isbn){
    	
        if(!bookRepo.existsById(isbn)) {
            throw new ResourceNotFoundException("Book", "isbn", isbn);
        }

        return repo.findByBookIsbn(isbn);
    }
    
    public List<BookReview> getBookReviewByISBN(String isbn,int reviewerid){
    	
        if(!bookRepo.existsById(isbn)) {
            throw new ResourceNotFoundException("Book", "isbn", isbn);
        }
        if(!reviewerRepo.existsById(reviewerid)) {
            throw new ResourceNotFoundException("Reviewer", "reviewerid", reviewerid);
        }
        

        return repo.findByBookIsbnAndReviewerReviewerID(isbn, reviewerid);
    }

   
    public List<BookReviewDTO> getBookReviewByISBNDTO(String isbn,int reviewerid){
    	
    	if(!bookRepo.existsById(isbn)) {
            throw new ResourceNotFoundException("Book", "isbn", isbn);
        }
    	if(!reviewerRepo.existsById(reviewerid)) {
            throw new ResourceNotFoundException("Reviewer", "reviewerid", reviewerid);
        }
    	
    	
        List<BookReview> reviews = repo.findByBookIsbnAndReviewerReviewerID(isbn, reviewerid);
        
        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException(
                "Review", "isbn & reviewerId", isbn + " , " + reviewerid);
        }
        
        return reviews.stream().map(review -> {
            BookReviewDTO dto = new BookReviewDTO();
            dto.setRating(review.getRating());
            dto.setComments(review.getComments());
            return dto;
        }).toList();
    }
    
    public List<BookReviewDTO> getBookReviewByISBNDTOisbn(String isbn){
    	
    	if(!bookRepo.existsById(isbn)) {
            throw new ResourceNotFoundException("Book", "isbn", isbn);
        }
    	
        List<BookReview> reviews = repo.findByBookIsbn(isbn);
        
        
        
        return reviews.stream().map(review -> {
            BookReviewDTO dto = new BookReviewDTO();
            dto.setRating(review.getRating());
            dto.setComments(review.getComments());
            return dto;
        }).toList();
    }
    
public List<BookReviewDTO> getBookReviewByISBNDTOreviewer(int reviewerid){
    	
    	if(!reviewerRepo.existsById(reviewerid)) {
            throw new ResourceNotFoundException("Book", "isbn", reviewerid);
        }
    	
        List<BookReview> reviews = repo.findByReviewerReviewerID(reviewerid);
        
        
        
        return reviews.stream().map(review -> {
            BookReviewDTO dto = new BookReviewDTO();
            dto.setRating(review.getRating());
            dto.setComments(review.getComments());
            return dto;
        }).toList();
    }

   
    public BookReview createReview(BookReview review) {

        
        Book book = bookRepo.findById(review.getBook().getIsbn())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Book not found with ISBN: " + review.getBook().getIsbn()));

        
        Reviewer reviewer = reviewerRepo.findById(review.getReviewer().getReviewerID())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Reviewer not found with ID: " + review.getReviewer().getReviewerID()));

        
        BookReviewId id = new BookReviewId(
            book.getIsbn(),
            reviewer.getReviewerID()
        );

        
        if(repo.existsById(id)) {
            throw new DuplicateResourceException(
                "Review already exists for this Book and Reviewer");
        }

        
        review.setBook(book);
        review.setReviewer(reviewer);
        review.setId(id);

        return repo.save(review);
    }

    
    public BookReview updateReview(BookReview review) {

        Book book = bookRepo.findById(review.getBook().getIsbn())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Book not found with ISBN: " + review.getBook().getIsbn()));

        Reviewer reviewer = reviewerRepo.findById(review.getReviewer().getReviewerID())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Reviewer not found with ID: " + review.getReviewer().getReviewerID()));

        BookReviewId id = new BookReviewId(
            book.getIsbn(),
            reviewer.getReviewerID()
        );

        
        if(!repo.existsById(id)) {
            throw new ResourceNotFoundException("Review not found to update");
        }

        review.setBook(book);
        review.setReviewer(reviewer);
        review.setId(id);

        return repo.save(review);
    }

    
    public void deleteReview(String isbn, Integer reviewerid) {

        BookReviewId id = new BookReviewId(isbn, reviewerid);

        
        if(!repo.existsById(id)) {
            throw new ResourceNotFoundException("Review not found to delete");
        }

        repo.deleteById(id);
    }

    
    public List<BookReview> getAllReviews(){
        return repo.findAll();
    }
}