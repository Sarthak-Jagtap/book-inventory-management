package com.bookinventory.category.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bookinventory.category.dto.CategoryRequestDTO;
import com.bookinventory.category.dto.CategoryResponseDTO;
import com.bookinventory.category.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@Validated
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@PostMapping("/admin/categories")
	public CategoryResponseDTO createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
		return categoryService.createCategory(dto);
	}

	@GetMapping("/categories")
	public List<CategoryResponseDTO> getAllCategories() {
		return categoryService.getAllCategories();
	}

	@GetMapping("/categories/{id}")
	public CategoryResponseDTO getCategoryById(@PathVariable int id) {
		return categoryService.getCategoryById(id);
	}

	@PutMapping("/admin/categories/{id}")
	public CategoryResponseDTO updateCategory(@PathVariable int id, @Valid @RequestBody CategoryRequestDTO dto) {
		return categoryService.updateCategory(id, dto);
	}

	@DeleteMapping("/admin/categories/{id}")
	public String deleteCategory(@PathVariable int id) {
		categoryService.deleteCategory(id);
		return "Category deleted successfully";
	}

}
