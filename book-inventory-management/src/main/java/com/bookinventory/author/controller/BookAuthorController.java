package com.bookinventory.author.controller;

import org.springframework.web.bind.annotation.*;

import com.bookinventory.author.dto.AuthorDTO;
import com.bookinventory.author.dto.BookAuthorDTO;
import com.bookinventory.author.entity.BookAuthor;
import com.bookinventory.author.service.BookAuthorService;
import com.bookinventory.user.common.response.ApiResponse;

import jakarta.validation.Valid;

import java.util.List;

@RestController
public class BookAuthorController {

    private final BookAuthorService service;

    public BookAuthorController(BookAuthorService service) {
        this.service = service;
    }

    // ASSIGN AUTHOR TO BOOK (STORE OWNER)
    @PostMapping("/api/v1/store-owner/book-authors")
    public ApiResponse<BookAuthor> assignAuthorToBook(@Valid @RequestBody BookAuthorDTO dto) {

        BookAuthor mapping = service.assignAuthorToBook(dto);

        return ApiResponse.success(201, "Author assigned to book successfully", mapping);
    }

    // GET AUTHORS OF A BOOK
    @GetMapping("/api/v1/books/{isbn}/authors")
    public ApiResponse<List<BookAuthor>> getAuthorsByBook(@PathVariable String isbn) {

        List<BookAuthor> authors = service.getAuthorsByBook(isbn);

        return ApiResponse.success(200, "Authors fetched successfully", authors);
    }

    // GET BOOKS OF AN AUTHOR
    @GetMapping("/api/v1/books/author/{authorId}")
    public ApiResponse<List<BookAuthor>> getBooksByAuthor(@PathVariable Integer authorId) {

        List<BookAuthor> books = service.getBooksByAuthor(authorId);

        return ApiResponse.success(200, "Books fetched successfully", books);
    }

    // UPDATE BOOK AUTHOR (STORE OWNER)
    @PutMapping("/api/v1/store-owner/book-authors/{isbn}/{authorId}")
    public ApiResponse<BookAuthor> updateBookAuthor(@PathVariable String isbn,
                                                    @PathVariable Integer authorId,
                                                    @Valid @RequestBody BookAuthorDTO dto) {

        BookAuthor updated = service.updateBookAuthor(isbn, authorId, dto);

        return ApiResponse.success(200, "Book author updated successfully", updated);
    }

    // REMOVE AUTHOR FROM BOOK (STORE OWNER)
    @DeleteMapping("/api/v1/store-owner/book-authors/{isbn}/{authorId}")
    public ApiResponse<Void> removeAuthorFromBook(@PathVariable String isbn,
                                                  @PathVariable Integer authorId) {

        service.removeAuthorFromBook(isbn, authorId);

        return ApiResponse.success(200, "Author removed from book successfully");
    }
    
    
    @GetMapping("/api/v1/books/{isbn}/primary-author")
    public ApiResponse<AuthorDTO> getPrimaryAuthor(@PathVariable String isbn) {

        AuthorDTO author = service.getPrimaryAuthor(isbn);

        return ApiResponse.success(200, "Primary author fetched successfully", author);
    }
}