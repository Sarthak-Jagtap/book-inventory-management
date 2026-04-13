package com.bookinventory.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookinventory.category.dto.CategoryRequestDTO;
import com.bookinventory.category.dto.CategoryResponseDTO;
import com.bookinventory.category.dto.CategorySummaryDTO;
import com.bookinventory.category.service.CategoryService;
import com.bookinventory.user.common.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // PUBLIC
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories() {
        return ResponseEntity.ok(
                ApiResponse.success(200, "Categories fetched successfully", categoryService.getAllCategories())
        );
    }
    
    @GetMapping("/categories/summary")
    public ResponseEntity<ApiResponse<List<CategorySummaryDTO>>> getCategorySummary() {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Category summary fetched successfully",
                        categoryService.getCategorySummary())
        );
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(
                ApiResponse.success(200, "Category fetched successfully", categoryService.getCategoryById(categoryId))
        );
    }

    // STORE OWNER
    @PostMapping("/store-owner/categories")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CategoryRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Category created successfully", categoryService.createCategory(dto)));
    }

    @PutMapping("/store-owner/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(
            @PathVariable Integer categoryId,
            @Valid @RequestBody CategoryRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Category updated successfully", categoryService.updateCategory(categoryId, dto))
        );
    }

    @DeleteMapping("/store-owner/categories/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer categoryId) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Category deleted successfully")
        );
    }
}