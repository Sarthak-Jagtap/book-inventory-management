package com.bookinventoryfrontend.author.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.bookinventoryfrontend.author.dto.AuthorDTO;
import com.bookinventoryfrontend.author.dto.BookAuthorDTO;
import com.bookinventoryfrontend.wrapper.ApiResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller

public class PageController {

	    // IMPORTANT: Point this to your backend server's address and port!
	    private final RestClient restClient = RestClient.create("http://localhost:8080");

	    @GetMapping("/index")
	    public String home() {
	        return "index";
	    }

	    @GetMapping("/api-author-dashboard")
	    public String apiDashboard(Model model) {
	        List<Endpoint> endpoints = Arrays.asList(
	            // Authors
	            new Endpoint("GET", "/api/v1/authors", "List all authors", "Returns a paginated list of all authors in the inventory."),
	            new Endpoint("GET", "/api/v1/authors/{id}", "Get author by id", "Fetch a single author's full profile by their unique numeric ID."),
	            new Endpoint("GET", "/api/v1/authors/search", "Search authors", "Full-text search across author first and last names."),
	            new Endpoint("GET", "/api/v1/authors/{id}/books", "Books by author", "Returns all books associated with the given author."),
	            new Endpoint("GET", "/api/v1/authors/{id}/stats", "Author statistics", "Returns aggregate stats for an author."),

	            // Book Authors
	            new Endpoint("GET", "/api/v1/books/{isbn}/authors", "Authors for book", "Lists all authors linked to a specific book ISBN."),
	            new Endpoint("GET", "/api/v1/books/author/{authorId}", "Books for author", "Returns all book records mapped to the given author ID."),
	            new Endpoint("GET", "/api/v1/books/{isbn}/primary-author", "Primary author lookup", "Quickly resolves which author is the designated primary author for a given ISBN."),

	            // Write Operations
	            new Endpoint("POST", "/api/v1/store-owner/authors", "Create author", "Creates a new author record. The authorID must be unique."),
	            new Endpoint("POST", "/api/v1/store-owner/book-authors", "Link book to author", "Creates a book–author mapping. Set primaryAuthor to 'Y'."),
	            new Endpoint("PUT", "/api/v1/store-owner/authors/{id}", "Update author", "Updates an existing author's first name, last name, and photo."),
	            new Endpoint("PUT", "/api/v1/store-owner/book-authors/{isbn}/{authorId}", "Update mapping", "Updates the primaryAuthor flag on an existing mapping."),
	            new Endpoint("DELETE", "/api/v1/store-owner/authors/{id}", "Delete author", "Permanently removes an author."),
	            new Endpoint("DELETE", "/api/v1/store-owner/book-authors/{isbn}/{authorId}", "Delete mapping", "Deletes a specific book–author link.")
	        );

	        model.addAttribute("endpoints", endpoints);
	        model.addAttribute("endpointCount", endpoints.size());
	        model.addAttribute("methodCount", endpoints.stream().map(Endpoint::getMethod).distinct().count());

	        return "/author/api-author-dashboard";
	    }

	    @GetMapping("/api-author-result")
	    public String apiResult(@RequestParam String endpoint, Model model) {
	        try {
	            // Proxies the GET request to the Backend API
	            Map<String, Object> response = restClient.get()
	                    .uri(endpoint)
	                    .retrieve()
	                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
	            
	            model.addAttribute("response", response);

	        } catch (RestClientResponseException e) {
	            // If the backend throws a 404/400 error, parse the JSON so the UI still looks nice
	            try {
	                Map<String, Object> errorResponse = e.getResponseBodyAs(new ParameterizedTypeReference<Map<String, Object>>() {});
	                model.addAttribute("response", errorResponse);
	            } catch (Exception parseEx) {
	                model.addAttribute("response", Map.of("success", false, "message", "Error: " + e.getStatusCode()));
	            }
	        } catch (Exception e) {
	            // Fallback if the backend is offline
	            model.addAttribute("response", Map.of("success", false, "message", "Backend unreachable: " + e.getMessage()));
	        }
	        return "/author/api-author-result";
	    }

	    // Helper Endpoint Class
	    public static class Endpoint {
	        private String method;
	        private String path;
	        private String title;
	        private String description;

	        public Endpoint(String method, String path, String title, String description) {
	            this.method = method;
	            this.path = path;
	            this.title = title;
	            this.description = description;
	        }

	        public String getMethod() { return method; }
	        public String getPath() { return path; }
	        public String getTitle() { return title; }
	        public String getDescription() { return description; }
	        
	        public String getFormattedPath() {
	            if (this.path == null) return "";
	            return this.path.replace("{", "<span class=\"param\">{").replace("}", "}</span>");
	        }
	    }
	    
	    
//	    ----------------------------------------------------------------------------------
	    
	    
	 // =========================================================================
	    // 1. AUTHORS LIST & SEARCH VIEW
	    // =========================================================================
	    @GetMapping("/authors")
	    public String listAuthors(@RequestParam(required = false) String name, Model model) {
	        String uri = (name != null && !name.isBlank()) 
	                ? "/api/v1/authors/search?name=" + name 
	                : "/api/v1/authors";

	        try {
	            ApiResponse<List<AuthorDTO>> response = restClient.get()
	                    .uri(uri)
	                    .retrieve()
	                    .body(new ParameterizedTypeReference<ApiResponse<List<AuthorDTO>>>() {});

	            if (response != null) {
	                model.addAttribute("authors", response.getData());
	            }
	        } catch (Exception e) {
	            model.addAttribute("error", "Failed to load authors.");
	        }
	        
	        // Keeps the search term in the search box after page reload
	        model.addAttribute("searchQuery", name); 
	        return "authors-list"; // HTML page 1
	    }

	    // =========================================================================
	    // 2. AUTHOR PROFILE VIEW (Details + Books + Stats)
	    // =========================================================================
	    @GetMapping("/authors/{id}")
	    public String authorProfile(@PathVariable Integer id, Model model) {
	        try {
	            // Fetch Author Details
	            ApiResponse<AuthorDTO> authorRes = restClient.get()
	                    .uri("/api/v1/authors/" + id).retrieve()
	                    .body(new ParameterizedTypeReference<ApiResponse<AuthorDTO>>() {});
	            model.addAttribute("author", authorRes.getData());

	            // Fetch Author's Books
	            ApiResponse<List<Map<String, Object>>> booksRes = restClient.get()
	                    .uri("/api/v1/authors/" + id + "/books").retrieve()
	                    .body(new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {});
	            model.addAttribute("books", booksRes.getData());

	            // Fetch Author Stats
	            ApiResponse<Map<String, Object>> statsRes = restClient.get()
	                    .uri("/api/v1/authors/" + id + "/stats").retrieve()
	                    .body(new ParameterizedTypeReference<ApiResponse<Map<String, Object>>>() {});
	            model.addAttribute("stats", statsRes.getData());

	        } catch (Exception e) {
	            model.addAttribute("error", "Failed to load author profile details.");
	        }
	        
	        return "author-profile"; // HTML page 2
	    }

	    // =========================================================================
	    // 3. SHOW CREATE / EDIT FORMS
	    // =========================================================================
	    
	    // Show empty form for creating an author
	    @GetMapping("/store-owner/authors/new")
	    public String showCreateForm(Model model) {
	        model.addAttribute("author", new AuthorDTO());
	        return "author-form"; // HTML page 3
	    }

	    // Show pre-filled form for editing an existing author
	    @GetMapping("/store-owner/authors/{id}/edit")
	    public String showEditForm(@PathVariable Integer id, Model model) {
	        try {
	            ApiResponse<AuthorDTO> response = restClient.get()
	                    .uri("/api/v1/authors/" + id).retrieve()
	                    .body(new ParameterizedTypeReference<ApiResponse<AuthorDTO>>() {});
	            model.addAttribute("author", response.getData());
	        } catch (Exception e) {
	            return "redirect:/authors?error=CouldNotLoadAuthor";
	        }
	        return "author-form"; // Reuses HTML page 3
	    }

	    // =========================================================================
	    // 4. FORM SUBMISSION ACTIONS (POST / PUT / DELETE)
	    // =========================================================================

	    // Catch the CREATE form submission
	    @PostMapping("/store-owner/authors")
	    public String submitCreateAuthor(@ModelAttribute AuthorDTO authorDTO) {
	        try {
	            restClient.post()
	                    .uri("/api/v1/store-owner/authors")
	                    .body(authorDTO)
	                    .retrieve()
	                    .toBodilessEntity();
	            return "redirect:/authors?success=Created";
	        } catch (Exception e) {
	            return "redirect:/store-owner/authors/new?error=CreateFailed";
	        }
	    }

	    // Catch the UPDATE form submission
	    @PostMapping("/store-owner/authors/{id}/update")
	    public String submitUpdateAuthor(@PathVariable Integer id, @ModelAttribute AuthorDTO authorDTO) {
	        try {
	            restClient.put()
	                    .uri("/api/v1/store-owner/authors/" + id)
	                    .body(authorDTO)
	                    .retrieve()
	                    .toBodilessEntity();
	            return "redirect:/authors/" + id + "?success=Updated";
	        } catch (Exception e) {
	            return "redirect:/store-owner/authors/" + id + "/edit?error=UpdateFailed";
	        }
	    }

	    // Catch the DELETE request
	    @PostMapping("/store-owner/authors/{id}/delete")
	    public String submitDeleteAuthor(@PathVariable Integer id) {
	        try {
	            restClient.delete()
	                    .uri("/api/v1/store-owner/authors/" + id)
	                    .retrieve()
	                    .toBodilessEntity();
	            return "redirect:/authors?success=Deleted";
	        } catch (Exception e) {
	            return "redirect:/authors/" + id + "?error=DeleteFailed";
	        }
	    }
	    
//	    ---------
	    
	 // =========================================================================
	    // 5. BOOK PROFILE (List Authors for a Book)
	    // =========================================================================
	    @GetMapping("/books/{isbn}")
	    public String bookProfile(@PathVariable String isbn, Model model) {
	        model.addAttribute("isbn", isbn);

	        try {
	            // Fetch authors assigned to this book
	            ApiResponse<List<Map<String, Object>>> authorsRes = restClient.get()
	                    .uri("/api/v1/books/" + isbn + "/authors").retrieve()
	                    .body(new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {});
	            model.addAttribute("bookAuthors", authorsRes.getData());

	            // Fetch primary author specifically
	            ApiResponse<AuthorDTO> primaryRes = restClient.get()
	                    .uri("/api/v1/books/" + isbn + "/primary-author").retrieve()
	                    .body(new ParameterizedTypeReference<ApiResponse<AuthorDTO>>() {});
	            model.addAttribute("primaryAuthor", primaryRes.getData());

	        } catch (Exception e) {
	            model.addAttribute("error", "Failed to load book-author details.");
	        }
	        return "book-profile";
	    }

	    // =========================================================================
	    // 6. SHOW ASSIGN / EDIT FORMS (For Book-Author Mapping)
	    // =========================================================================
	    
	    // Show form to assign an author to a book
	    @GetMapping("/store-owner/book-authors/new")
	    public String showAssignAuthorForm(@RequestParam(required = false) String isbn, Model model) {
	        BookAuthorDTO dto = new BookAuthorDTO();
	        if (isbn != null) {
	            dto.setIsbn(isbn); // Pre-fill ISBN if navigating from the book profile
	        }
	        model.addAttribute("bookAuthor", dto);
	        return "author/book-author-form";
	    }

	    // Show form to edit an existing mapping (e.g., changing primary author status)
	    @GetMapping("/store-owner/book-authors/{isbn}/{authorId}/edit")
	    public String showEditBookAuthorForm(@PathVariable String isbn, @PathVariable Integer authorId, Model model) {
	        BookAuthorDTO dto = new BookAuthorDTO();
	        dto.setIsbn(isbn);
	        dto.setAuthorID(authorId);
	        model.addAttribute("bookAuthor", dto);
	        return "book-author-form";
	    }

	    // =========================================================================
	    // 7. MAPPING FORM ACTIONS (POST / PUT / DELETE)
	    // =========================================================================

	    // Catch the CREATE (Assign) mapping form submission
	    @PostMapping("/store-owner/book-authors")
	    public String submitAssignAuthor(@ModelAttribute BookAuthorDTO dto) {
	        try {
	            restClient.post()
	                    .uri("/api/v1/store-owner/book-authors")
	                    .body(dto)
	                    .retrieve()
	                    .toBodilessEntity();
	            return "redirect:/books/" + dto.getIsbn() + "?success=Author+Assigned";
	        } catch (Exception e) {
	            return "redirect:/store-owner/book-authors/new?isbn=" + dto.getIsbn() + "&error=Assignment+Failed";
	        }
	    }

	    // Catch the UPDATE mapping form submission
	    @PostMapping("/store-owner/book-authors/{isbn}/{authorId}/update")
	    public String submitUpdateBookAuthor(@PathVariable String isbn, @PathVariable Integer authorId, @ModelAttribute BookAuthorDTO dto) {
	        try {
	            restClient.put()
	                    .uri("/api/v1/store-owner/book-authors/" + isbn + "/" + authorId)
	                    .body(dto)
	                    .retrieve()
	                    .toBodilessEntity();
	            return "redirect:/books/" + isbn + "?success=Mapping+Updated";
	        } catch (Exception e) {
	            return "redirect:/store-owner/book-authors/" + isbn + "/" + authorId + "/edit?error=Update+Failed";
	        }
	    }

	    // Catch the DELETE (Remove Author) request
	    @PostMapping("/store-owner/book-authors/{isbn}/{authorId}/delete")
	    public String submitRemoveAuthor(@PathVariable String isbn, @PathVariable Integer authorId) {
	        try {
	            restClient.delete()
	                    .uri("/api/v1/store-owner/book-authors/" + isbn + "/" + authorId)
	                    .retrieve()
	                    .toBodilessEntity();
	            return "redirect:/books/" + isbn + "?success=Author+Removed";
	        } catch (Exception e) {
	            return "redirect:/books/" + isbn + "?error=Remove+Failed";
	        }
	    }
	    
	}

