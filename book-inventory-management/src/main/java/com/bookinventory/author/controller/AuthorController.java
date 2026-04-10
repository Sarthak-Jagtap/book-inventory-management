package com.bookinventory.author.controller;


import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.bookinventory.author.dto.AuthorDTO;
import com.bookinventory.author.service.AuthorService;
import com.bookinventory.book.entity.Book;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // CREATE AUTHOR
    @PostMapping

    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO dto) {
        return ResponseEntity.ok(authorService.createAuthor(dto));
    
    }

    // GET ALL AUTHORS
    @GetMapping
    public List<AuthorDTO> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    // GET AUTHOR BY ID
    @GetMapping("/{id}")
    public AuthorDTO getAuthor(@PathVariable Integer id) {
        return authorService.getAuthorById(id);
    }

    // UPDATE AUTHOR
    @PutMapping("/{id}")
    public AuthorDTO updateAuthor(@PathVariable Integer id,
                                  @RequestBody AuthorDTO dto) {
        return authorService.updateAuthor(id, dto);
    }

    // DELETE AUTHOR
    @DeleteMapping("/{id}")
    public String deleteAuthor(@PathVariable Integer id) {

        authorService.deleteAuthor(id);

        return "Author deleted successfully";
    }
    
    //SEARCH AUTHOR
    @GetMapping("/search")
    public ResponseEntity<List<AuthorDTO>> searchAuthors(@RequestParam String name) {

        List<AuthorDTO> authors = authorService.searchAuthors(name);

        return ResponseEntity.ok(authors);
    }
    
    //FETCH BOOKS OF AUTHORS
    @GetMapping("/{id}/books")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Integer id) {

        return ResponseEntity.ok(authorService.getBooksByAuthor(id));
    }
    
    //NO.S OF BOOKS OF AUTHORS - STATS
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getAuthorStats(@PathVariable Integer id) {

        return ResponseEntity.ok(authorService.getAuthorStats(id));
    }
}
