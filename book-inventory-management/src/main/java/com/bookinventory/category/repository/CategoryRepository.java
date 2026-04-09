package com.bookinventory.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

	
}
