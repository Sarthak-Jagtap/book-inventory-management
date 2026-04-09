package com.bookinventory.author.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.author.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
	
}
