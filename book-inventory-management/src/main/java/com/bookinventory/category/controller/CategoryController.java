package com.bookinventory.category.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookinventory.category.entity.Category;
import com.bookinventory.category.service.CategoryService;

@RestController
@RequestMapping("category")
public class CategoryController {

	@Autowired
	private CategoryService service;
	
	@GetMapping
	public List<Category> getAllCategory() {
		return service.getAllCategories();
	}
}
