package com.bookinventory.book.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.bookinventory.book.dto.BookRequestDTO;
import com.bookinventory.book.dto.BookResponseDTO;
import com.bookinventory.book.service.BookService;

import jakarta.validation.Valid;

@RestController
@Validated
public class BookController {

	@Autowired
	private BookService bookService;

	@PostMapping("/admin/books")
	public BookResponseDTO createBook(@Valid @RequestBody BookRequestDTO dto) {
		return bookService.createBook(dto);
	}

	@GetMapping("/books")
	public List<BookResponseDTO> getAllBooks() {
		return bookService.getAllBooks();
	}

	@GetMapping("/books/{isbn}")
	public BookResponseDTO getBookById(@PathVariable String isbn) {
		return bookService.getBookById(isbn);
	}

	@PutMapping("/admin/books/{isbn}")
	public BookResponseDTO updateBook(@PathVariable String isbn, @Valid @RequestBody BookRequestDTO dto) {
		return bookService.updateBook(isbn, dto);
	}

	@DeleteMapping("/admin/books/{isbn}")
	public String deleteBook(@PathVariable String isbn) {
		bookService.deleteBook(isbn);
		return "Book deleted successfully";
	}
}