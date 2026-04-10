package com.bookinventory.book.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookinventory.book.entity.Book;
import com.bookinventory.book.service.BookService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/book")
public class BookController {

	@Autowired
	private BookService service;
	
	@GetMapping()
	public List<Book> showAllBooks() {
		return service.getAllBooks();
	}
	
}
