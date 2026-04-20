package com.bookinventory.reviewer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.bookinventory.reviewer.dto.ReviewerDTO;
import com.bookinventory.reviewer.entity.Reviewer;
import com.bookinventory.reviewer.service.ReviewerService;
import com.bookinventory.user.common.response.ApiResponse;

@ExtendWith(MockitoExtension.class)
public class ReviewerControllerTest {

    @Mock
    private ReviewerService service;

    @InjectMocks
    private ReviewerController controller;

    // ✅ 1. GET by reviewer ID
    @Test
    void testGetReviewerById() {

        List<ReviewerDTO> list = Arrays.asList(
                new ReviewerDTO(),
                new ReviewerDTO()
        );

        Mockito.when(service.getReviewerByReviewerIDDTO(1))
                .thenReturn(list);

        List<ReviewerDTO> result = controller.getReviewer(1, null);

        assertEquals(2, result.size());
    }

    // ✅ 2. GET by name
    @Test
    void testGetReviewerByName() {

        List<ReviewerDTO> list = Arrays.asList(new ReviewerDTO());

        Mockito.when(service.getReviewerByName("John"))
                .thenReturn(list);

        List<ReviewerDTO> result = controller.getReviewerByName("John");

        assertEquals(1, result.size());
    }

    // ✅ 3. GET by company
    @Test
    void testGetReviewerByCompany() {

        List<ReviewerDTO> list = Arrays.asList(new ReviewerDTO());

        Mockito.when(service.getReviewerByCompany("TCS"))
                .thenReturn(list);

        List<ReviewerDTO> result = controller.getReviewerByCompany("TCS");

        assertEquals(1, result.size());
    }

    // ✅ 4. COUNT reviewers
    @Test
    void testGetReviewerCount() {

        List<ReviewerDTO> list = Arrays.asList(
                new ReviewerDTO(),
                new ReviewerDTO()
        );

        Mockito.when(service.getReviewerByCompany("TCS"))
                .thenReturn(list);

        ResponseEntity<ApiResponse<String>> response =
                controller.getReviewsCount("TCS");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    // ✅ 5. GET all reviewers
    @Test
    void testGetAllReviewers() {

        List<ReviewerDTO> list = Arrays.asList(new ReviewerDTO(), new ReviewerDTO());

        Mockito.when(service.getAllReviewers()).thenReturn(list);

        List<ReviewerDTO> result = controller.getAllReviewer();

        assertEquals(2, result.size());
    }

    // ✅ 6. CREATE reviewer
    @Test
    void testCreateReviewer() {

        ReviewerDTO reviewerdto = new ReviewerDTO();

        Mockito.when(service.createReviewer(Mockito.any()))
                .thenReturn(reviewerdto);

        ReviewerDTO result = controller.createReviewer(reviewerdto);

        assertNotNull(result);
    }

    // ✅ 7. UPDATE reviewer
    @Test
    void testUpdateReviewer() {

        ReviewerDTO reviewerdto = new ReviewerDTO();

        Mockito.when(service.updateReviewer(Mockito.any()))
                .thenReturn(reviewerdto);

        ReviewerDTO result = controller.updateReviewer(reviewerdto);

        assertNotNull(result);
    }

    // ✅ 8. DELETE reviewer
    @Test
    void testDeleteReviewer() {

        Mockito.doNothing().when(service).deleteReviewer(1);

        String result = controller.deleteReviewer(1);

        assertEquals("Reviewer deleted Succesfully", result);
    }
}