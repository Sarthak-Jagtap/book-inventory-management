package com.bookinventory.category.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.bookinventory.category.dto.CategoryRequestDTO;
import com.bookinventory.category.dto.CategoryResponseDTO;
import com.bookinventory.category.dto.CategorySummaryDTO;
import com.bookinventory.category.service.CategoryService;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.user.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private JwtUtil jwtUtil;

    // GET ALL CATEGORIES
    @Test
    void testGetAllCategories() throws Exception {

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setCategoryId(1);
        dto.setCategoryDescription("Fiction");

        when(categoryService.getAllCategories()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].categoryDescription").value("Fiction"));
    }

    // GET CATEGORY SUMMARY
    @Test
    void testGetCategorySummary() throws Exception {

        CategorySummaryDTO summary = new CategorySummaryDTO("Fiction", 5L);

        when(categoryService.getCategorySummary()).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/categories/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].category").value("Fiction"))
                .andExpect(jsonPath("$.data[0].bookCount").value(5));
    }

    // GET CATEGORY BY ID - SUCCESS
    @Test
    void testGetCategoryById() throws Exception {

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setCategoryId(1);
        dto.setCategoryDescription("Fiction");

        when(categoryService.getCategoryById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryDescription").value("Fiction"));
    }

    // GET CATEGORY BY ID - NOT FOUND
    @Test
    void testGetCategoryById_NotFound() throws Exception {

        when(categoryService.getCategoryById(1))
                .thenThrow(new ResourceNotFoundException("Category", "id", 1));

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isNotFound());
    }

    // CREATE CATEGORY
    @Test
    void testCreateCategory() throws Exception {

        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setCategoryId(1);
        request.setCategoryDescription("Fiction");

        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setCategoryId(1);
        response.setCategoryDescription("Fiction");

        when(categoryService.createCategory(any(CategoryRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/store-owner/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.categoryDescription").value("Fiction"));
    }

    // UPDATE CATEGORY - SUCCESS
    @Test
    void testUpdateCategory() throws Exception {

        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setCategoryId(1);
        request.setCategoryDescription("Updated Category");

        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setCategoryId(1);
        response.setCategoryDescription("Updated Category");

        when(categoryService.updateCategory(eq(1), any(CategoryRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/store-owner/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryDescription").value("Updated Category"));
    }

    // UPDATE CATEGORY - NOT FOUND
    @Test
    void testUpdateCategory_NotFound() throws Exception {

        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setCategoryDescription("Updated Category");

        when(categoryService.updateCategory(eq(1), any(CategoryRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Category", "id", 1));

        mockMvc.perform(put("/api/v1/store-owner/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // DELETE CATEGORY - SUCCESS
    @Test
    void testDeleteCategory() throws Exception {

        doNothing().when(categoryService).deleteCategory(1);

        mockMvc.perform(delete("/api/v1/store-owner/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }

    // DELETE CATEGORY - NOT FOUND
    @Test
    void testDeleteCategory_NotFound() throws Exception {

        doThrow(new ResourceNotFoundException("Category", "id", 1))
                .when(categoryService).deleteCategory(1);

        mockMvc.perform(delete("/api/v1/store-owner/categories/1"))
                .andExpect(status().isNotFound());
    }
}