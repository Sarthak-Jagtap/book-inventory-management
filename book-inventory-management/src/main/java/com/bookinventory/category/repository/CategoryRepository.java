package com.bookinventory.category.repository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.category.dto.CategorySummaryDTO;
import com.bookinventory.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

	@Query("""
		    SELECT new com.bookinventory.category.dto.CategorySummaryDTO(
		        c.catDescription, COUNT(b)
		    )
		    FROM Category c
		    LEFT JOIN Book b ON b.category.catId = c.catId
		    GROUP BY c.catId, c.catDescription
		""")
		List<CategorySummaryDTO> getCategorySummary();
}
