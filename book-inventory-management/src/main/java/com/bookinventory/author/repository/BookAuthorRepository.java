package com.bookinventory.author.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bookinventory.author.entity.BookAuthor;
import com.bookinventory.author.entity.BookAuthorId;
import com.bookinventory.book.entity.Book;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, BookAuthorId> {

	List<BookAuthor> findByIdAuthorID(Integer authorID);
	
	List<BookAuthor> findByIdISBN(String isbn);
	
 Optional<BookAuthor> findByIdISBNAndPrimaryAuthor(String isbn, String primaryAuthor);



}
