package com.bookinventory.bookreview.controller;

import com.bookinventory.bookreview.dto.BookReviewDTO;
import com.bookinventory.bookreview.entity.BookReview;
import com.bookinventory.bookreview.service.BookReviewService;
import com.bookinventory.user.common.response.ApiResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookReviewControllerTest {

    @Mock
    private BookReviewService service;

    @InjectMocks
    private BookReviewController controller;

    // ✅ 1. GET reviews by ISBN
    @Test
    void testGetReviewsByIsbn() {

        List<BookReviewDTO> list = Arrays.asList(
                new BookReviewDTO(5, "Good"),
                new BookReviewDTO(4, "Nice")
        );

        Mockito.when(service.getBookReviewByISBNDTOisbn("123"))
                .thenReturn(list);

        List<BookReviewDTO> result = controller.getReviewsByIsbn("123");

        assertEquals(2, result.size());
    }

    // ✅ 2. GET reviews count
    @Test
    void testGetReviewsCount() {

        List<BookReviewDTO> list = Arrays.asList(
                new BookReviewDTO(5, "Good")
        );

        Mockito.when(service.getBookReviewByISBNDTOisbn("123"))
                .thenReturn(list);

        ResponseEntity<ApiResponse<String>> response =
                controller.getReviewsCount("123");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());   // ✅ fix
    }

    // ✅ 3. GET reviews by reviewer
    @Test
    void testGetReviewsByReviewer() {

        List<BookReviewDTO> list = Arrays.asList(
                new BookReviewDTO(3, "Okay")
        );

        Mockito.when(service.getBookReviewByISBNDTOreviewer(1))
                .thenReturn(list);

        List<BookReviewDTO> result = controller.getReviewsByReviewerid(1);

        assertEquals(1, result.size());
    }

    @Test
    void testReviewerCount() {

        List<BookReviewDTO> list = Arrays.asList(
                new BookReviewDTO(3, "Okay")
        );

        Mockito.when(service.getBookReviewByISBNDTOreviewer(1))
                .thenReturn(list);

        ResponseEntity<ApiResponse<String>> response =
                controller.getReviewerReviewsCount(1);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());   // ✅ fix
    }

    // ✅ 5. GET particular review
    @Test
    void testGetParticularReview() {

        List<BookReviewDTO> list = Arrays.asList(
                new BookReviewDTO(5, "Excellent")
        );

        Mockito.when(service.getBookReviewByISBNDTO("123", 1))
                .thenReturn(list);

        List<BookReviewDTO> result =
                controller.getParticulerReviews("123", 1);

        assertEquals(1, result.size());
    }

    // ✅ 6. GET all reviews
    @Test
    void testGetAllReviews() {

        List<BookReview> list = Arrays.asList(new BookReview(), new BookReview());

        Mockito.when(service.getAllReviews()).thenReturn(list);

        List<BookReview> result = controller.getAllReviews();

        assertEquals(2, result.size());
    }

    // ✅ 7. CREATE review
    @Test
    void testCreateReview() {

        BookReview review = new BookReview();
        review.setRating(5);
        review.setComments("Awesome");

        Mockito.when(service.createReview(Mockito.any()))
                .thenReturn(review);

        BookReview result = controller.createReview(review);

        assertEquals(5, result.getRating());
        assertEquals("Awesome", result.getComments());
    }

    // ✅ 8. UPDATE review
    @Test
    void testUpdateReview() {

        BookReview review = new BookReview();
        review.setRating(4);
        review.setComments("Updated");

        Mockito.when(service.updateReview(Mockito.any()))
                .thenReturn(review);

        BookReview result = controller.updateReview(review);

        assertEquals(4, result.getRating());
    }

    // ✅ 9. DELETE review
    @Test
    void testDeleteReview() {

        Mockito.doNothing().when(service).deleteReview("123", 1);

        String result = controller.deleteReview("123", 1);

        assertEquals("Review Deleted Successfully", result);
    }
}