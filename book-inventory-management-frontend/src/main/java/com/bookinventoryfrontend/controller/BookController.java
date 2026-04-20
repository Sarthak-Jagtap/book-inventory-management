package com.bookinventoryfrontend.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookinventoryfrontend.dto.BookDTO;
import com.bookinventoryfrontend.dto.CategoryDTO;
import com.bookinventoryfrontend.dto.PageResponseDTO;
import com.bookinventoryfrontend.dto.PublisherDTO;
import com.bookinventoryfrontend.service.BookService;
import com.bookinventoryfrontend.service.CategoryService;
import com.bookinventoryfrontend.service.PublisherService;

@Controller
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final PublisherService publisherService;

    public BookController(BookService bookService,
                          CategoryService categoryService,
                          PublisherService publisherService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.publisherService = publisherService;
    }

    @GetMapping("/books/dashboard")
    public String bookDashboard() {
        return "book/dashboard";
    }

    @GetMapping("/books")
    public String getAllBooks(Model model) {

        List<BookDTO> books = bookService.getAllBooks();

        Map<Integer, String> categoryMap = categoryService.getAllCategories()
                .stream()
                .collect(Collectors.toMap(
                        CategoryDTO::getCategoryId,
                        CategoryDTO::getCategoryDescription
                ));

        Map<Integer, String> publisherMap = publisherService.getAllPublishers()
                .stream()
                .collect(Collectors.toMap(
                        PublisherDTO::getPublisherId,
                        PublisherDTO::getName
                ));

        model.addAttribute("books", books);
        model.addAttribute("categoryMap", categoryMap);
        model.addAttribute("publisherMap", publisherMap);

        return "book/list";
    }
    
    @GetMapping("/books/isbn")
    public String showIsbnForm() {
        return "book/isbn-form";
    }

    @GetMapping("/books/details")
    public String getBookDetails(@RequestParam String isbn, Model model) {

        model.addAttribute("book", bookService.getBookByIsbn(isbn));

        return "book/details";
    }
    
    @GetMapping("/books/search")
    public String searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer publisherId,
            Model model) {

        if (title != null || categoryId != null || publisherId != null) {
            model.addAttribute("books",
                    bookService.searchBooks(title, categoryId, publisherId));
        }

        return "book/search";
    }
    
    @GetMapping("/books/category")
    public String getBooksByCategory(
            @RequestParam Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        PageResponseDTO<BookDTO> pageData =
                bookService.getBooksByCategory(categoryId, page, size);

        model.addAttribute("books", pageData.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("categoryId", categoryId);

        return "book/category";
    }
    
    @GetMapping("/books/publisher")
    public String getBooksByPublisher(
            @RequestParam Integer publisherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        PageResponseDTO<BookDTO> pageData =
                bookService.getBooksByPublisher(publisherId, page, size);

        model.addAttribute("books", pageData.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("publisherId", publisherId);

        return "book/publisher";
    }
}