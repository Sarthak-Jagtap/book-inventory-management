package com.bookinventory.inventory.controller;

import com.bookinventory.inventory.entity.BookCondition;
import com.bookinventory.inventory.repository.BookConditionRepository;
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
    public List<BookCondition> getAllConditions() {
        return repository.findAll();
    }

    @GetMapping("/{rank}")
    public BookCondition getConditionByRank(@PathVariable Integer rank) {
        return repository.getConditionByRank(rank)
                .orElseThrow(() -> new RuntimeException("Condition not found for rank: " + rank));
    }
}