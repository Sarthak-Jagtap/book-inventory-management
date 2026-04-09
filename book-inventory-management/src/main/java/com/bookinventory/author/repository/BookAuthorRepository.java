package com.bookinventory.author.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.author.entity.BookAuthor;
import com.bookinventory.author.entity.BookAuthorId;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, BookAuthorId> {

	List<BookAuthor> findByIdAuthorID(Integer authorID);
	
	List<BookAuthor> findByIdISBN(String isbn);
    

}
