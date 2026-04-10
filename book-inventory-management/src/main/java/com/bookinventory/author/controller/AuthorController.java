package com.bookinventory.author.controller;


import org.springframework.web.bind.annotation.*;

import com.bookinventory.author.dto.AuthorDTO;
import com.bookinventory.author.service.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // CREATE AUTHOR
    @PostMapping
    public AuthorDTO createAuthor(@RequestBody AuthorDTO dto) {
        return authorService.createAuthor(dto);
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
}
