package com.bookinventory.inventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookinventory.inventory.entity.BookCondition;
import com.bookinventory.inventory.repository.BookConditionRepository;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private BookConditionRepository repo;

    @GetMapping("/conditions")
    public List<BookCondition> getAll() {
        return repo.findAll();
    }
}