package com.bookinventory.category.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.category.entity.Category;
import com.bookinventory.category.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	public Category addCategory(Category category) {
		return categoryRepository.save(category);
	}

	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	public Category getCategoryById(int id) {
		// Exception Handling Needed
		return categoryRepository.findById(id).orElse(null);
	}

	public Category updateCategory(int id, String newDescription) {
		// Exception Handling Needed
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category not found"));

		category.setCatDescription(newDescription);
		return categoryRepository.save(category);
	}

	public void deleteCategory(int id) {
		// Exception Handling Needed
		categoryRepository.deleteById(id);
	}
}
