package com.bookinventory.author.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bookinventory.author.entity.Author;

import com.bookinventory.book.entity.Book;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    List<Author> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    
    @Query("""
    	       SELECT COUNT(ba)
    	       FROM BookAuthor ba
    	       WHERE ba.id.authorID = :authorId
    	       """)
    	Long countBooksByAuthor(Integer authorId);
	 @Query("""
	           SELECT b
	           FROM BookAuthor ba
	           JOIN Book b ON ba.id.ISBN = b.isbn
	           WHERE ba.id.authorID = :authorId
	           """)
	    List<Book> findBooksByAuthorId(Integer authorId);
	 
	
}
