package com.bookinventoryfrontend.controller;

import com.bookinventoryfrontend.dto.CategoryDTO;
import com.bookinventoryfrontend.dto.CategoryRequestDTO;
import com.bookinventoryfrontend.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }    

    // GET ALL
    @GetMapping("/categories/list")
    public String getAllCategories(Model model) {

        model.addAttribute("categories", categoryService.getAllCategories());

        return "category/list";
    }

    // FORM
    @GetMapping("/categories/id")
    public String showCategoryForm() {
        return "category/id-form";
    }

    // GET BY ID
    @GetMapping("/categories/details")
    public String getCategoryById(@RequestParam Integer categoryId, Model model) {

        model.addAttribute("category", categoryService.getCategoryById(categoryId));

        return "category/details";
    }
    
    // GET Category Summary
    @GetMapping("/categories/summary")
    public String getCategorySummary(Model model) {

        model.addAttribute("summaryList", categoryService.getCategorySummary());

        return "category/summary";
    }
    
    // POST Create Category
    @GetMapping("/categories/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new CategoryRequestDTO());
        return "category/add";
    }

    @PostMapping("/categories/add")
    public String createCategory(@ModelAttribute CategoryRequestDTO dto, Model model) {

        CategoryDTO created = categoryService.createCategory(dto);

        model.addAttribute("category", created);

        return "category/details";
    }
    
    // PUT Update Category
    @GetMapping("/categories/update")
    public String showUpdateForm() {
        return "category/update";
    }

    @PostMapping("/categories/update")
    public String updateCategory(
            @RequestParam int categoryId,
            @RequestParam String categoryDescription,
            Model model) {

        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setCategoryId(categoryId);
        dto.setCategoryDescription(categoryDescription);

        CategoryDTO updated = categoryService.updateCategory(categoryId, dto);

        model.addAttribute("category", updated);

        return "category/details";
    }
    
    // DELETE Delete Category
    @GetMapping("/categories/delete")
    public String showDeleteForm() {
        return "category/delete";
    }

    @PostMapping("/categories/delete")
    public String deleteCategory(@RequestParam int categoryId, Model model) {

        categoryService.deleteCategory(categoryId);

        model.addAttribute("message", "Category deleted successfully");

        return "category/result";
    }
}