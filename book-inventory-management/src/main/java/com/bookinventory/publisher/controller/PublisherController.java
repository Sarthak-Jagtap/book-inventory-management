package com.bookinventory.publisher.controller;

import com.bookinventory.publisher.dto.PublisherRequestDTO;
import com.bookinventory.publisher.dto.PublisherResponseDTO;
import com.bookinventory.publisher.service.PublisherService;
import com.bookinventory.user.common.response.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    // PUBLIC
    @GetMapping("/publishers")
    public ResponseEntity<ApiResponse<List<PublisherResponseDTO>>> getAllPublishers() {
        return ResponseEntity.ok(
                ApiResponse.success(200, "Publishers fetched successfully", publisherService.getAllPublishers())
        );
    }

    @GetMapping("/publishers/{publisherId}")
    public ResponseEntity<ApiResponse<PublisherResponseDTO>> getPublisherById(@PathVariable Integer publisherId) {
        return ResponseEntity.ok(
                ApiResponse.success(200, "Publisher fetched successfully", publisherService.getPublisherById(publisherId))
        );
    }

    // STORE OWNER
    @PostMapping("/store-owner/publishers")
    public ResponseEntity<ApiResponse<PublisherResponseDTO>> createPublisher(
            @Valid @RequestBody PublisherRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Publisher created successfully", publisherService.createPublisher(dto)));
    }

    @PutMapping("/store-owner/publishers/{publisherId}")
    public ResponseEntity<ApiResponse<PublisherResponseDTO>> updatePublisher(
            @PathVariable Integer publisherId,
            @Valid @RequestBody PublisherRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Publisher updated successfully", publisherService.updatePublisher(publisherId, dto))
        );
    }

    @DeleteMapping("/store-owner/publishers/{publisherId}")
    public ResponseEntity<ApiResponse<Void>> deletePublisher(@PathVariable Integer publisherId) {
        publisherService.deletePublisher(publisherId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Publisher deleted successfully")
        );
    }
}