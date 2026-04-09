package com.bookinventory.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, String> {

}
