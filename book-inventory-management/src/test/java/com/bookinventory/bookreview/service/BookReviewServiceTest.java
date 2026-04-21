package com.bookinventory.bookreview.service;

import com.bookinventory.book.entity.Book;
import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.bookreview.dto.BookReviewDTO;
import com.bookinventory.bookreview.entity.BookReview;
import com.bookinventory.bookreview.entity.BookReviewId;
import com.bookinventory.bookreview.repository.BookReviewRepository;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.common.exception.DuplicateResourceException;
import com.bookinventory.reviewer.entity.Reviewer;
import com.bookinventory.reviewer.repository.ReviewerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class BookReviewServiceTest {

    @Mock
    private BookReviewRepository repo;

    @Mock
    private ReviewerRepository reviewerRepo;

    @Mock
    private BookRepository bookRepo;

    @InjectMocks
    private BookReviewService service;

    // ✅ 1. GET reviews by ISBN
    @Test
    void testGetBookReviewByISBNDTOisbn() {

        // 🔥 create Book
        Book book = new Book();
        book.setIsbn("123");

        // 🔥 create Reviewer
        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerID(1);

        // 🔥 attach to review
        BookReview review = new BookReview();
        review.setBook(book);
        review.setReviewer(reviewer);
        review.setRating(5);
        review.setComments("Good");

        Mockito.when(bookRepo.existsById("123")).thenReturn(true);
        Mockito.when(repo.findByBookIsbn("123")).thenReturn(List.of(review));

        List<BookReviewDTO> result = service.getBookReviewByISBNDTOisbn("123");

        assertEquals(1, result.size());
        assertEquals("123", result.get(0).getIsbn());
    }

    // ❌ EXCEPTION case
    @Test
    void testGetBookReviewByISBNDTOisbn_NotFound() {

        Mockito.when(bookRepo.existsById("123")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getBookReviewByISBNDTOisbn("123");
        });
    }

    // ✅ 2. CREATE review
    @Test
    void testCreateReview() {

        Book book = new Book();
        book.setIsbn("123");

        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerID(1);

        BookReview savedReview = new BookReview();
        savedReview.setBook(book);
        savedReview.setReviewer(reviewer);
        savedReview.setRating(5);
        savedReview.setComments("Good");

        BookReviewDTO dto = new BookReviewDTO("123", 1, 5, "Good");

        Mockito.when(bookRepo.findById("123")).thenReturn(Optional.of(book));
        Mockito.when(reviewerRepo.findById(1)).thenReturn(Optional.of(reviewer));
        Mockito.when(repo.existsById(Mockito.any())).thenReturn(false);
        Mockito.when(repo.save(Mockito.any())).thenReturn(savedReview);

        BookReviewDTO result = service.createReview(dto);

        assertNotNull(result);
        assertEquals("123", result.getIsbn());
    }

    // ❌ DUPLICATE EXCEPTION
    @Test
    void testCreateReview_Duplicate() {

        Book book = new Book();
        book.setIsbn("123");

        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerID(1);

        BookReviewDTO dto = new BookReviewDTO("123", 1, 5, "Good");

        Mockito.when(bookRepo.findById("123")).thenReturn(Optional.of(book));
        Mockito.when(reviewerRepo.findById(1)).thenReturn(Optional.of(reviewer));
        Mockito.when(repo.existsById(Mockito.any())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            service.createReview(dto);
        });
    }

    // ✅ 3. UPDATE review
    @Test
    void testUpdateReview() {

        Book book = new Book();
        book.setIsbn("123");

        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerID(1);

        BookReview updatedReview = new BookReview();
        updatedReview.setBook(book);
        updatedReview.setReviewer(reviewer);
        updatedReview.setRating(4);
        updatedReview.setComments("Updated");

        BookReviewDTO dto = new BookReviewDTO("123", 1, 4, "Updated");

        Mockito.when(bookRepo.findById("123")).thenReturn(Optional.of(book));
        Mockito.when(reviewerRepo.findById(1)).thenReturn(Optional.of(reviewer));
        Mockito.when(repo.existsById(Mockito.any())).thenReturn(true);
        Mockito.when(repo.save(Mockito.any())).thenReturn(updatedReview);

        BookReviewDTO result = service.updateReview(dto);

        assertNotNull(result);
        assertEquals(4, result.getRating());
    }

    // ❌ UPDATE NOT FOUND
    @Test
    void testUpdateReview_NotFound() {

        Book book = new Book();
        book.setIsbn("123");

        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerID(1);

        BookReviewDTO dto = new BookReviewDTO("123", 1, 5, "Test");

        Mockito.when(bookRepo.findById("123")).thenReturn(Optional.of(book));
        Mockito.when(reviewerRepo.findById(1)).thenReturn(Optional.of(reviewer));
        Mockito.when(repo.existsById(Mockito.any())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            service.updateReview(dto);
        });
    }

    // ✅ 4. DELETE review
    @Test
    void testDeleteReview() {

        Mockito.when(repo.existsById(Mockito.any())).thenReturn(true);

        service.deleteReview("123", 1);

        Mockito.verify(repo).deleteById(Mockito.any());
    }

    // ❌ DELETE NOT FOUND
    @Test
    void testDeleteReview_NotFound() {

        Mockito.when(repo.existsById(Mockito.any())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteReview("123", 1);
        });
    }

    // ✅ 5. GET ALL
    @Test
    void testGetAllReviewsDTO() {

        Book book = new Book();
        book.setIsbn("123");

        Reviewer reviewer = new Reviewer();
        reviewer.setReviewerID(1);

        BookReview review = new BookReview();
        review.setBook(book);
        review.setReviewer(reviewer);
        review.setRating(5);
        review.setComments("Nice");

        Mockito.when(repo.findAll()).thenReturn(List.of(review));

        List<BookReviewDTO> result = service.getAllReviewsDTO();

        assertEquals(1, result.size());
    }
}