package com.bookinventory.author.controller;

import org.springframework.web.bind.annotation.*;

import com.bookinventory.author.dto.AuthorDTO;
import com.bookinventory.author.service.AuthorService;
import com.bookinventory.book.entity.Book;
import com.bookinventory.user.common.response.ApiResponse;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // CREATE AUTHOR (STORE OWNER)
    @PostMapping("/api/v1/store-owner/authors")
    public ApiResponse<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO dto) {

        AuthorDTO author = authorService.createAuthor(dto);

        return ApiResponse.success(201, "Author created successfully", author);
    }

    // GET ALL AUTHORS
    @GetMapping("/api/v1/authors")
    public ApiResponse<List<AuthorDTO>> getAllAuthors() {

        List<AuthorDTO> authors = authorService.getAllAuthors();

        return ApiResponse.success(200, "Authors fetched successfully", authors);
    }

    // GET AUTHOR BY ID
    @GetMapping("/api/v1/authors/{authorId}")
    public ApiResponse<AuthorDTO> getAuthor(@PathVariable Integer authorId) {

        AuthorDTO author = authorService.getAuthorById(authorId);

        return ApiResponse.success(200, "Author fetched successfully", author);
    }

    // UPDATE AUTHOR (STORE OWNER)
    @PutMapping("/api/v1/store-owner/authors/{authorId}")
    public ApiResponse<AuthorDTO> updateAuthor(@PathVariable Integer authorId,
                                               @Valid @RequestBody AuthorDTO dto) {

        AuthorDTO author = authorService.updateAuthor(authorId, dto);

        return ApiResponse.success(200, "Author updated successfully", author);
    }

    // DELETE AUTHOR (STORE OWNER)
    @DeleteMapping("/api/v1/store-owner/authors/{authorId}")
    public ApiResponse<Void> deleteAuthor(@PathVariable Integer authorId) {

        authorService.deleteAuthor(authorId);

        return ApiResponse.success(200, "Author deleted successfully");
    }

    // SEARCH AUTHOR
    @GetMapping("/api/v1/authors/search")
    public ApiResponse<List<AuthorDTO>> searchAuthors(@RequestParam String name) {

        List<AuthorDTO> authors = authorService.searchAuthors(name);

        return ApiResponse.success(200, "Search completed", authors);
    }

    // FETCH BOOKS OF AUTHOR
    @GetMapping("/api/v1/authors/{authorId}/books")
    public ApiResponse<List<Book>> getBooksByAuthor(@PathVariable Integer authorId) {

        List<Book> books = authorService.getBooksByAuthor(authorId);

        return ApiResponse.success(200, "Books fetched successfully", books);
    }

    // AUTHOR STATS
    @GetMapping("/api/v1/authors/{authorId}/stats")
    public ApiResponse<Map<String, Object>> getAuthorStats(@PathVariable Integer authorId) {

        Map<String, Object> stats = authorService.getAuthorStats(authorId);

        return ApiResponse.success(200, "Author stats fetched successfully", stats);
    }
}