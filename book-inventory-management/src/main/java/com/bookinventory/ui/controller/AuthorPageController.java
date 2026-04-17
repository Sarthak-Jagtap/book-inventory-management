package com.bookinventory.ui.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.bookinventory.ui.model.Endpoint;
@Controller
public class AuthorPageController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/api-author-dashboard")
    public String apiDashboard(Model model) {

        List<Endpoint> endpoints = Arrays.asList(
            // Authors
            new Endpoint("GET", "/api/v1/authors", "List all authors", "Returns a paginated list of all authors in the inventory. Useful for populating dropdowns or building author indexes."),
            new Endpoint("GET", "/api/v1/authors/{id}", "Get author by id", "Fetch a single author's full profile by their unique numeric ID. Returns 404 if the author does not exist."),
            new Endpoint("GET", "/api/v1/authors/search", "Search authors", "Full-text search across author first and last names. Supports partial matches — searching 'Gos' will match 'Gosling'."),
            new Endpoint("GET", "/api/v1/authors/{id}/books", "Books by author", "Returns all books associated with the given author. Includes books where the author is listed as primary or secondary contributor."),
            new Endpoint("GET", "/api/v1/authors/{id}/stats", "Author statistics", "Returns aggregate stats for an author — total books, primary authorship count, genres covered, and inventory metrics."),

            // Book Authors
            new Endpoint("GET", "/api/v1/books/{isbn}/authors", "Authors for book", "Lists all authors linked to a specific book ISBN, including their role (primary / co-author)."),
            new Endpoint("GET", "/api/v1/books/author/{authorId}", "Books for author", "Returns all book records mapped to the given author ID — a reverse lookup from the /authors/{id}/books route that returns full book detail objects."),
            new Endpoint("GET", "/api/v1/books/{isbn}/primary-author", "Primary author lookup", "Quickly resolves which author is the designated primary author for a given ISBN. Returns a single Author object."),

            // Write Operations
            new Endpoint("POST", "/api/v1/store-owner/authors", "Create author", "Creates a new author record. The authorID must be unique. The photo field accepts a URL string or empty string."),
            new Endpoint("POST", "/api/v1/store-owner/book-authors", "Link book to author", "Creates a book–author mapping. Set primaryAuthor to 'Y' for the main author. Only one primary author per ISBN is allowed."),
            new Endpoint("PUT", "/api/v1/store-owner/authors/{id}", "Update author", "Updates an existing author's first name, last name, and photo. Provide all fields — this is a full replacement, not a partial patch."),
            new Endpoint("PUT", "/api/v1/store-owner/book-authors/{isbn}/{authorId}", "Update mapping", "Updates the primaryAuthor flag on an existing book–author mapping. Toggle between 'Y' and 'N'."),
            new Endpoint("DELETE", "/api/v1/store-owner/authors/{id}", "Delete author", "Permanently removes an author. Will fail if the author still has active book–author mappings — remove those first."),
            new Endpoint("DELETE", "/api/v1/store-owner/book-authors/{isbn}/{authorId}", "Delete mapping", "Deletes a specific book–author link. Does not delete either the book or the author — only the association between them.")
       );

        model.addAttribute("endpoints", endpoints);
        model.addAttribute("endpointCount", endpoints.size());
        
        // Calculate distinct methods for the dynamic hero stats
        long methodCount = endpoints.stream().map(Endpoint::getMethod).distinct().count();
        model.addAttribute("methodCount", methodCount);

        return "/author/api-author-dashboard";
    }
    
    @GetMapping("/api-author-result")
    public String viewApi(@RequestParam String endpoint, Model model) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080" + endpoint;

        Map response = restTemplate.getForObject(url, Map.class);

        model.addAttribute("response", response);

        return "/author/api-author-result";
    }
}