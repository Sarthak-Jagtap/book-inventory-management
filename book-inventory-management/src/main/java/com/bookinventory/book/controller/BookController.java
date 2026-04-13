package com.bookinventory.book.controller;

import com.bookinventory.book.dto.BookDetailsResponseDTO;
import com.bookinventory.book.dto.BookRequestDTO;
import com.bookinventory.book.dto.BookResponseDTO;
import com.bookinventory.book.service.BookService;
import com.bookinventory.user.common.response.ApiResponse;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public ResponseEntity<ApiResponse<Page<BookResponseDTO>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BookResponseDTO> books = bookService.getAllBooks(page, size);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Books fetched successfully", books)
        );
    }

    @GetMapping("/books/{isbn}")
    public ResponseEntity<ApiResponse<BookResponseDTO>> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(
                ApiResponse.success(200, "Book fetched successfully", bookService.getBookById(isbn))
        );
    }

    @PostMapping("/store-owner/books")
    public ResponseEntity<ApiResponse<BookResponseDTO>> createBook(
            @Valid @RequestBody BookRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Book created successfully", bookService.createBook(dto)));
    }

    @PutMapping("/store-owner/books/{isbn}")
    public ResponseEntity<ApiResponse<BookResponseDTO>> updateBook(
            @PathVariable String isbn,
            @Valid @RequestBody BookRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.success(200, "Book updated successfully", bookService.updateBook(isbn, dto))
        );
    }

    @DeleteMapping("/store-owner/books/{isbn}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable String isbn) {
        bookService.deleteBook(isbn);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Book deleted successfully")
        );
    }
    
    @GetMapping("/books/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<BookResponseDTO>>> getBooksByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BookResponseDTO> books = bookService.getBooksByCategory(categoryId, page, size);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Books fetched by category successfully", books)
        );
    }
    
    
    @GetMapping("/books/publisher/{publisherId}")
    public ResponseEntity<ApiResponse<Page<BookResponseDTO>>> getBooksByPublisher(
            @PathVariable Integer publisherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BookResponseDTO> books = bookService.getBooksByPublisher(publisherId, page, size);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Books fetched by publisher successfully", books)
        );
    }
    
    @GetMapping("/books/search")
    public ResponseEntity<ApiResponse<List<BookResponseDTO>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer publisherId) {

        List<BookResponseDTO> books = bookService.searchBooks(title, categoryId, publisherId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Books fetched successfully", books)
        );
    }
    
    @GetMapping("/books/{isbn}/details")
    public ResponseEntity<ApiResponse<BookDetailsResponseDTO>> getBookDetails(
            @PathVariable String isbn) {

        BookDetailsResponseDTO response = bookService.getBookDetails(isbn);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Book details fetched successfully", response)
        );
    }
}