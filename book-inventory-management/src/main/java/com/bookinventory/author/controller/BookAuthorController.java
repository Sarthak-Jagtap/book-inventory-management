package com.bookinventory.author.controller;

import org.springframework.web.bind.annotation.*;

import com.bookinventory.author.dto.BookAuthorDTO;
import com.bookinventory.author.entity.BookAuthor;
import com.bookinventory.author.service.BookAuthorService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/book-authors")
public class BookAuthorController {

    private final BookAuthorService service;

    public BookAuthorController(BookAuthorService service) {
        this.service = service;
    }

    // ASSIGN AUTHOR TO BOOK
    @PostMapping
    public BookAuthor assignAuthorToBook(@Valid @RequestBody BookAuthorDTO dto) {
        return service.assignAuthorToBook(dto);
    }

    // GET AUTHORS OF A BOOK
    @GetMapping("/book/{isbn}")
    public List<BookAuthor> getAuthorsByBook(@PathVariable String isbn) {
        return service.getAuthorsByBook(isbn);
    }

    // GET BOOKS OF AN AUTHOR
    @GetMapping("/author/{authorId}")
    public List<BookAuthor> getBooksByAuthor(@PathVariable Integer authorId) {
        return service.getBooksByAuthor(authorId);
    }

    // REMOVE AUTHOR FROM BOOK
    @DeleteMapping
    public String removeAuthorFromBook(@RequestParam String isbn,
                                       @RequestParam Integer authorId) {

        service.removeAuthorFromBook(isbn, authorId);

        return "Author removed from book";
    }
}