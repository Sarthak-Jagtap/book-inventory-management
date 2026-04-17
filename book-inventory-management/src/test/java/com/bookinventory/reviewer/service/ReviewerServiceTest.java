package com.bookinventory.reviewer.service;

import com.bookinventory.reviewer.dto.ReviewerDTO;
import com.bookinventory.reviewer.entity.Reviewer;
import com.bookinventory.reviewer.repository.ReviewerRepository;
import com.bookinventory.common.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class ReviewerServiceTest {

    @Mock
    private ReviewerRepository repo;

    @InjectMocks
    private ReviewerService service;

    // ✅ 1. GET by reviewer ID (DTO)
    @Test
    void testGetReviewerByReviewerIDDTO() {

        Reviewer r = new Reviewer();
        r.setReviewerID(1);
        r.setName("John");
        r.setEmployedBy("TCS");

        Mockito.when(repo.findByReviewerID(1)).thenReturn(List.of(r));

        List<ReviewerDTO> result = service.getReviewerByReviewerIDDTO(1);

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    // ❌ NOT FOUND (ID)
    @Test
    void testGetReviewerByReviewerIDDTO_NotFound() {

        Mockito.when(repo.findByReviewerID(1)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getReviewerByReviewerIDDTO(1);
        });
    }

    // ✅ 2. GET by NAME
    @Test
    void testGetReviewerByName() {

        Reviewer r = new Reviewer();
        r.setReviewerID(1);
        r.setName("John");
        r.setEmployedBy("Infosys");

        Mockito.when(repo.findByName("John")).thenReturn(List.of(r));

        List<ReviewerDTO> result = service.getReviewerByName("John");

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    // ❌ NOT FOUND (NAME)
    @Test
    void testGetReviewerByName_NotFound() {

        Mockito.when(repo.findByName("John")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getReviewerByName("John");
        });
    }

    // ✅ 3. GET by COMPANY
    @Test
    void testGetReviewerByCompany() {

        Reviewer r = new Reviewer();
        r.setReviewerID(1);
        r.setName("Amit");
        r.setEmployedBy("TCS");

        Mockito.when(repo.findByEmployedBy("TCS")).thenReturn(List.of(r));

        List<ReviewerDTO> result = service.getReviewerByCompany("TCS");

        assertEquals(1, result.size());
        assertEquals("TCS", result.get(0).getEmployedBy());
    }

    // ❌ NOT FOUND (COMPANY)
    @Test
    void testGetReviewerByCompany_NotFound() {

        Mockito.when(repo.findByEmployedBy("TCS")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getReviewerByCompany("TCS");
        });
    }

    // ✅ 4. CREATE reviewer
    @Test
    void testCreateReviewer() {

        Reviewer reviewer = new Reviewer();

        Mockito.when(repo.save(Mockito.any())).thenReturn(reviewer);

        Reviewer result = service.createReviewer(reviewer);

        assertNotNull(result);
    }

    // ✅ 5. UPDATE reviewer
    @Test
    void testUpdateReviewer() {

        Reviewer reviewer = new Reviewer();

        Mockito.when(repo.save(Mockito.any())).thenReturn(reviewer);

        Reviewer result = service.updateReviewer(reviewer);

        assertNotNull(result);
    }

    // ✅ 6. DELETE reviewer
    @Test
    void testDeleteReviewer() {

        Mockito.doNothing().when(repo).deleteById(1);

        service.deleteReviewer(1);

        Mockito.verify(repo).deleteById(1);
    }

    // ✅ 7. GET ALL
    @Test
    void testGetAllReviewers() {

        Mockito.when(repo.findAll()).thenReturn(List.of(new Reviewer(), new Reviewer()));

        List<Reviewer> result = service.getAllReviewers();

        assertEquals(2, result.size());
    }
}