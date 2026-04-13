package com.bookinventory.inventory.controller;

import com.bookinventory.inventory.entity.BookCondition;
import com.bookinventory.inventory.repository.BookConditionRepository;
import com.bookinventory.user.common.exception.ResourceNotFoundException;
import com.bookinventory.user.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/store-owner/book-conditions")
public class BookConditionController {

    private final BookConditionRepository repository;

    public BookConditionController(BookConditionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookCondition>>> getAllConditions() {
        List<BookCondition> conditions = repository.findAll();

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.OK.value(), "Book conditions fetched successfully", conditions),
                HttpStatus.OK
        );
    }

    @GetMapping("/{rank}")
    public ResponseEntity<ApiResponse<BookCondition>> getConditionByRank(@PathVariable Integer rank) {
        BookCondition condition = repository.getConditionByRank(rank)
                .orElseThrow(() -> new ResourceNotFoundException("BookCondition", "rank", rank));

        return new ResponseEntity<>(
        		ApiResponse.success(HttpStatus.OK.value(), "Book condition fetched successfully", condition),
                HttpStatus.OK
        );
    }
}