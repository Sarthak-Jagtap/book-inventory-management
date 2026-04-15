package com.bookinventory.bookreview.controller;

import com.bookinventory.bookreview.service.BookReviewService;
import com.bookinventory.bookreview.dto.BookReviewDTO;
import com.bookinventory.bookreview.entity.BookReview;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookReviewControllerTest {

    @Mock
    private BookReviewService service;

    @InjectMocks
    private BookReviewController controller;


@Test
void testGetReviewsByIsbn() {
    String isbn = "123";

    List<BookReviewDTO> mockList = List.of(new BookReviewDTO());

    when(service.getBookReviewByISBNDTOisbn(isbn)).thenReturn(mockList);

    List<BookReviewDTO> result = controller.getReviewsByIsbn(isbn);

    assertEquals(1, result.size());
    verify(service, times(1)).getBookReviewByISBNDTOisbn(isbn);
}

@Test
void testGetReviewsCount() {
    String isbn = "123";

    when(service.getBookReviewByISBNDTOisbn(isbn))
            .thenReturn(List.of(new BookReviewDTO(), new BookReviewDTO()));

    var response = controller.getReviewsCount(isbn);

    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody().getMessage().contains("2"));
}

@Test
void testGetReviewsByReviewer() {
    int reviewerId = 1;

    when(service.getBookReviewByISBNDTOreviewer(reviewerId))
            .thenReturn(List.of(new BookReviewDTO()));

    List<BookReviewDTO> result = controller.getReviewsByReviewerid(reviewerId);

    assertEquals(1, result.size());
}

@Test
void testCreateReview() {
    BookReview review = new BookReview();

    when(service.createReview(review)).thenReturn(review);

    BookReview result = controller.createReview(review);

    assertNotNull(result);
    verify(service).createReview(review);
}

@Test
void testUpdateReview() {
    BookReview review = new BookReview();

    when(service.updateReview(review)).thenReturn(review);

    BookReview result = controller.updateReview(review);

    assertNotNull(result);
    verify(service).updateReview(review);
}

@Test
void testDeleteReview() {
    String isbn = "123";
    Integer reviewerId = 1;

    doNothing().when(service).deleteReview(isbn, reviewerId);

    String result = controller.deleteReview(isbn, reviewerId);

    assertEquals("Review Deleted Successfully", result);
    verify(service).deleteReview(isbn, reviewerId);
}

}