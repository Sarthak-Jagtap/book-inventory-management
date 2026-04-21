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
import com.bookinventory.common.exception.DuplicateResourceException;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.reviewer.entity.Reviewer;
import com.bookinventory.reviewer.repository.ReviewerRepository;

@Service
public class BookReviewService {
    
    @Autowired
    private BookReviewRepository repo;

    @Autowired
    private ReviewerRepository reviewerRepo;

    @Autowired
    private BookRepository bookRepo;

    // ================= ENTITY METHODS =================

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

    // ================= DTO METHODS (🔥 FIXED) =================

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

        return reviews.stream().map(review -> new BookReviewDTO(
                review.getBook().getIsbn(),
                review.getReviewer().getReviewerID(),
                review.getRating(),
                review.getComments()
        )).toList();
    }
    
    public List<BookReviewDTO> getBookReviewByISBNDTOisbn(String isbn){
        
        if(!bookRepo.existsById(isbn)) {
            throw new ResourceNotFoundException("Book", "isbn", isbn);
        }

        List<BookReview> reviews = repo.findByBookIsbn(isbn);

        return reviews.stream().map(review -> new BookReviewDTO(
                review.getBook().getIsbn(),
                review.getReviewer().getReviewerID(),
                review.getRating(),
                review.getComments()
        )).toList();
    }
    
    public List<BookReviewDTO> getBookReviewByISBNDTOreviewer(int reviewerid){
        
        if(!reviewerRepo.existsById(reviewerid)) {
            throw new ResourceNotFoundException("Reviewer", "reviewerid", reviewerid);
        }

        List<BookReview> reviews = repo.findByReviewerReviewerID(reviewerid);

        return reviews.stream().map(review -> new BookReviewDTO(
                review.getBook().getIsbn(),
                review.getReviewer().getReviewerID(),
                review.getRating(),
                review.getComments()
        )).toList();
    }

    // ================= CREATE =================

    public BookReviewDTO createReview(BookReviewDTO dto) {

        Book book = bookRepo.findById(dto.getIsbn())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with ISBN: " + dto.getIsbn()));

        Reviewer reviewer = reviewerRepo.findById(dto.getReviewerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reviewer not found with ID: " + dto.getReviewerId()));

        BookReviewId id = new BookReviewId(
                book.getIsbn(),
                reviewer.getReviewerID()
        );

        if (repo.existsById(id)) {
            throw new DuplicateResourceException(
                    "Review already exists for this Book and Reviewer");
        }

        // ✅ DTO → ENTITY conversion
        BookReview review = new BookReview();
        review.setId(id);
        review.setBook(book);
        review.setReviewer(reviewer);
        review.setRating(dto.getRating());
        review.setComments(dto.getComments());

        // ✅ SAVE ENTITY
        BookReview saved = repo.save(review);

        // ✅ ENTITY → DTO
        return new BookReviewDTO(
                saved.getBook().getIsbn(),
                saved.getReviewer().getReviewerID(),
                saved.getRating(),
                saved.getComments()
        );
    }

    // ================= UPDATE =================

    public BookReviewDTO updateReview(BookReviewDTO dto) {

        Book book = bookRepo.findById(dto.getIsbn())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with ISBN: " + dto.getIsbn()));

        Reviewer reviewer = reviewerRepo.findById(dto.getReviewerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reviewer not found with ID: " + dto.getReviewerId()));

        BookReviewId id = new BookReviewId(
                book.getIsbn(),
                reviewer.getReviewerID()
        );

        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Review not found to update");
        }

        // ✅ DTO → ENTITY
        BookReview review = new BookReview();
        review.setId(id);
        review.setBook(book);
        review.setReviewer(reviewer);
        review.setRating(dto.getRating());
        review.setComments(dto.getComments());

        BookReview updated = repo.save(review);

        // ✅ ENTITY → DTO
        return new BookReviewDTO(
                updated.getBook().getIsbn(),
                updated.getReviewer().getReviewerID(),
                updated.getRating(),
                updated.getComments()
        );
    }

    // ================= DELETE =================

    public void deleteReview(String isbn, Integer reviewerid) {

        BookReviewId id = new BookReviewId(isbn, reviewerid);

        if(!repo.existsById(id)) {
            throw new ResourceNotFoundException("Review not found to delete");
        }

        repo.deleteById(id);
    }

    // ================= GET ALL =================

    public List<BookReviewDTO> getAllReviewsDTO() {
        return repo.findAll()
                .stream()
                .map(review -> new BookReviewDTO(
                        review.getBook().getIsbn(),
                        review.getReviewer().getReviewerID(),
                        review.getRating(),
                        review.getComments()
                ))
                .toList();
    }
}