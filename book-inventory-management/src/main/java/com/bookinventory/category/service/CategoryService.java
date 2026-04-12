package com.bookinventory.category.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.category.dto.CategoryRequestDTO;
import com.bookinventory.category.dto.CategoryResponseDTO;
import com.bookinventory.category.entity.Category;
import com.bookinventory.category.repository.CategoryRepository;
import com.bookinventory.user.common.exception.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {

		Category category = convertToEntity(dto);
		Category savedCategory = categoryRepository.save(category);

		return convertToDTO(savedCategory);
	}

	public List<CategoryResponseDTO> getAllCategories() {

		List<Category> categories = categoryRepository.findAll();

		return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	public CategoryResponseDTO getCategoryById(int id) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

		return convertToDTO(category);
	}
	
	public CategoryResponseDTO updateCategory(int id, CategoryRequestDTO dto) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        existingCategory.setCatDescription(dto.getCategoryDescription());

        Category updatedCategory = categoryRepository.save(existingCategory);

        return convertToDTO(updatedCategory);
    }

	public void deleteCategory(int id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        categoryRepository.delete(category);
    }
	
	private Category convertToEntity(CategoryRequestDTO dto) {
		Category category = new Category();
		category.setCatId(dto.getCategoryId()); // IMPORTANT (manual ID)
		category.setCatDescription(dto.getCategoryDescription());
		return category;
	}

	private CategoryResponseDTO convertToDTO(Category category) {
		CategoryResponseDTO dto = new CategoryResponseDTO();
		dto.setCategoryId(category.getCatId());
		dto.setCategoryDescription(category.getCatDescription());
		return dto;
	}
}
