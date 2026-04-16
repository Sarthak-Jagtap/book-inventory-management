package com.bookinventory.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookinventory.category.dto.CategoryRequestDTO;
import com.bookinventory.category.dto.CategoryResponseDTO;
import com.bookinventory.category.dto.CategorySummaryDTO;
import com.bookinventory.category.entity.Category;
import com.bookinventory.category.repository.CategoryRepository;
import com.bookinventory.common.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryService categoryService;

	private Category category;
	private CategoryRequestDTO requestDTO;

	@BeforeEach
	void setUp() {
		category = new Category();
		category.setCatId(1);
		category.setCatDescription("Fiction");

		requestDTO = new CategoryRequestDTO();
		requestDTO.setCategoryId(1);
		requestDTO.setCategoryDescription("Fiction");
	}

	// Create Category
	@Test
	void testCreateCategory() {
		when(categoryRepository.save(any(Category.class))).thenReturn(category);

		CategoryResponseDTO responseDTO = categoryService.createCategory(requestDTO);

		assertNotNull(responseDTO);
		assertEquals(1, responseDTO.getCategoryId());
		assertEquals("Fiction", responseDTO.getCategoryDescription());

		verify(categoryRepository).save(any(Category.class));
	}

	// Get All Categories
	@Test
	void testGetAllCategories() {

		when(categoryRepository.findAll()).thenReturn(List.of(category));

		List<CategoryResponseDTO> result = categoryService.getAllCategories();

		assertEquals(1, result.size());
		assertEquals("Fiction", result.get(0).getCategoryDescription());

		verify(categoryRepository).findAll();
	}

	// Get Category By Id - Success
	@Test
	void testGetCategoryById_Success() {

		when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

		CategoryResponseDTO result = categoryService.getCategoryById(1);

		assertNotNull(result);
		assertEquals("Fiction", result.getCategoryDescription());

		verify(categoryRepository).findById(1);
	}

	// Get Category by Id - Not Found
	@Test
	void testGetCategoryById_NotFound() {

		when(categoryRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			categoryService.getCategoryById(1);
		});

		verify(categoryRepository).findById(1);
	}

	// Update Category - Success
	@Test
	void testUpdateCategory_Success() {

		CategoryRequestDTO updatedDTO = new CategoryRequestDTO();
		updatedDTO.setCategoryDescription("Non-Fiction");

		when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

		when(categoryRepository.save(any(Category.class))).thenReturn(category);

		CategoryResponseDTO result = categoryService.updateCategory(1, updatedDTO);

		assertEquals("Non-Fiction", result.getCategoryDescription());

		verify(categoryRepository).findById(1);
		verify(categoryRepository).save(category);
	}

	// Update Category - Not Found
	@Test
	void testUpdateCategory_NotFound() {

		when(categoryRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			categoryService.updateCategory(1, requestDTO);
		});

		verify(categoryRepository).findById(1);
	}

	// Delete Category - Success
	@Test
	void testDeleteCategory_Success() {

		when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

		doNothing().when(categoryRepository).delete(category);

		categoryService.deleteCategory(1);

		verify(categoryRepository).findById(1);
		verify(categoryRepository).delete(category);
	}

	// Delete Category - Not Found
	@Test
	void testDeleteCategory_NotFound() {

		when(categoryRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			categoryService.deleteCategory(1);
		});

		verify(categoryRepository).findById(1);
	}

	// Get Category Summary
	@Test
	void testGetCategorySummary() {

		CategorySummaryDTO summary = new CategorySummaryDTO("Fiction", 5L);

		when(categoryRepository.getCategorySummary()).thenReturn(List.of(summary));

		List<CategorySummaryDTO> result = categoryService.getCategorySummary();

		assertEquals(1, result.size());
		assertEquals("Fiction", result.get(0).getCategory());
		assertEquals(5L, result.get(0).getBookCount());

		verify(categoryRepository).getCategorySummary();
	}
}