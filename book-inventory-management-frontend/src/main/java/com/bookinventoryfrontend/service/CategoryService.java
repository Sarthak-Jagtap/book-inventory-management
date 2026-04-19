package com.bookinventoryfrontend.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.bookinventoryfrontend.dto.ApiResponseDTO;
import com.bookinventoryfrontend.dto.CategoryDTO;
import com.bookinventoryfrontend.dto.CategoryRequestDTO;
import com.bookinventoryfrontend.dto.CategorySummaryDTO;

@Service
public class CategoryService {

    private final RestClient restClient;

    public CategoryService(RestClient restClient) {
        this.restClient = restClient;
    }

    // GET ALL
    public List<CategoryDTO> getAllCategories() {

        ApiResponseDTO<CategoryDTO[]> response = restClient.get()
                .uri("/api/v1/categories")
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<CategoryDTO[]>>() {});

        return Arrays.asList(response.getData());
    }

    // GET BY ID
    public CategoryDTO getCategoryById(Integer categoryId) {

        ApiResponseDTO<CategoryDTO> response = restClient.get()
                .uri("/api/v1/categories/" + categoryId)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<CategoryDTO>>() {});

        return response.getData();
    }
    
    // GET Category Summary
    public List<CategorySummaryDTO> getCategorySummary() {

        ApiResponseDTO<CategorySummaryDTO[]> response = restClient.get()
                .uri("/api/v1/categories/summary")
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<CategorySummaryDTO[]>>() {});

        return Arrays.asList(response.getData());
    }
    
    // POST Create Category
    public CategoryDTO createCategory(CategoryRequestDTO dto) {

        ApiResponseDTO<CategoryDTO> response = restClient.post()
                .uri("/api/v1/store-owner/categories")
                .body(dto)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<CategoryDTO>>() {});

        return response.getData();
    }
    
    // PUT Update Category
    public CategoryDTO updateCategory(int id, CategoryRequestDTO dto) {

        ApiResponseDTO<CategoryDTO> response = restClient.put()
                .uri("/api/v1/store-owner/categories/" + id)
                .body(dto)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<CategoryDTO>>() {});

        return response.getData();
    }
    
    // DELETE Delete Category
    public void deleteCategory(int id) {

        restClient.delete()
                .uri("/api/v1/store-owner/categories/" + id)
                .retrieve()
                .toBodilessEntity();
    }
    
}