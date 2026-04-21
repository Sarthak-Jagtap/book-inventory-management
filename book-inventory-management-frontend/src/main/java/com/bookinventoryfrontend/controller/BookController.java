package com.bookinventoryfrontend.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookinventoryfrontend.dto.BookDTO;
import com.bookinventoryfrontend.dto.BookDetailsDTO;
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

	public BookController(BookService bookService, CategoryService categoryService, PublisherService publisherService) {
		this.bookService = bookService;
		this.categoryService = categoryService;
		this.publisherService = publisherService;
	}

	@GetMapping("/books/dashboard")
	public String bookDashboard() {
		return "book/dashboard";
	}

	// GET All Books
	@GetMapping("/books")
	public String getAllBooks(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			Model model) {

		PageResponseDTO<BookDTO> pageData = bookService.getAllBooks(page, size);

		Map<Integer, String> categoryMap = categoryService.getAllCategories().stream()
				.collect(Collectors.toMap(CategoryDTO::getCategoryId, CategoryDTO::getCategoryDescription));

		Map<Integer, String> publisherMap = publisherService.getAllPublishers().stream()
				.collect(Collectors.toMap(PublisherDTO::getPublisherId, PublisherDTO::getName));

		model.addAttribute("books", pageData.getContent());
		model.addAttribute("categoryMap", categoryMap);
		model.addAttribute("publisherMap", publisherMap);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pageData.getTotalPages());

		return "book/list";
	}

	// GET ISBN form
	@GetMapping("/books/isbn")
	public String showIsbnForm() {
		return "book/isbn-form";
	}

	// GET Book Details
	@GetMapping("/books/details")
	public String getBookDetails(@RequestParam String isbn, @RequestParam(required = false) String msg, Model model) {

		BookDetailsDTO book = bookService.getBookByIsbn(isbn);
		model.addAttribute("book", book);

		if (msg != null) {
			model.addAttribute("successMessage", msg);
		}

		return "book/details";
	}

	@GetMapping("/books/search")
	public String searchBooks(@RequestParam(required = false) String title,
			@RequestParam(required = false) Integer categoryId, @RequestParam(required = false) Integer publisherId,
			Model model) {

		model.addAttribute("categories", categoryService.getAllCategories());
		model.addAttribute("publishers", publisherService.getAllPublishers());

		if (title != null || categoryId != null || publisherId != null) {

			List<BookDTO> books = bookService.searchBooks(title, categoryId, publisherId);

			Map<Integer, String> categoryMap = categoryService.getAllCategories().stream()
					.collect(Collectors.toMap(CategoryDTO::getCategoryId, CategoryDTO::getCategoryDescription));

			Map<Integer, String> publisherMap = publisherService.getAllPublishers().stream()
					.collect(Collectors.toMap(PublisherDTO::getPublisherId, PublisherDTO::getName));

			model.addAttribute("books", books);
			model.addAttribute("categoryMap", categoryMap);
			model.addAttribute("publisherMap", publisherMap);
		}

		return "book/search";
	}

	// GET Books by Category
	@GetMapping("/books/category")
	public String getBooksByCategory(
	        @RequestParam(required = false) Integer categoryId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size,
	        Model model) {

	    PageResponseDTO<BookDTO> pageData;

	    if (categoryId == null) {
	        pageData = bookService.getAllBooks(page, size);
	    } else {
	        pageData = bookService.getBooksByCategory(categoryId, page, size);
	    }

	    Map<Integer, String> categoryMap = categoryService.getAllCategories().stream()
	            .collect(Collectors.toMap(CategoryDTO::getCategoryId, CategoryDTO::getCategoryDescription));

	    Map<Integer, String> publisherMap = publisherService.getAllPublishers().stream()
	            .collect(Collectors.toMap(PublisherDTO::getPublisherId, PublisherDTO::getName));

	    model.addAttribute("books", pageData.getContent());
	    model.addAttribute("categoryMap", categoryMap);
	    model.addAttribute("publisherMap", publisherMap);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", pageData.getTotalPages());
	    model.addAttribute("categoryId", categoryId);

	    return "book/category";
	}

	@GetMapping("/books/publisher")
	public String getBooksByPublisher(
	        @RequestParam(required = false) Integer publisherId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size,
	        Model model) {

	    PageResponseDTO<BookDTO> pageData;

	    if (publisherId == null) {
	        pageData = bookService.getAllBooks(page, size);
	    } else {
	        pageData = bookService.getBooksByPublisher(publisherId, page, size);
	    }

	    Map<Integer, String> categoryMap = categoryService.getAllCategories().stream()
	            .collect(Collectors.toMap(CategoryDTO::getCategoryId, CategoryDTO::getCategoryDescription));

	    Map<Integer, String> publisherMap = publisherService.getAllPublishers().stream()
	            .collect(Collectors.toMap(PublisherDTO::getPublisherId, PublisherDTO::getName));

	    model.addAttribute("books", pageData.getContent());
	    model.addAttribute("categoryMap", categoryMap);
	    model.addAttribute("publisherMap", publisherMap);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", pageData.getTotalPages());
	    model.addAttribute("publisherId", publisherId);

	    return "book/publisher";
	}

	// GET Show Add Book Form
	@GetMapping("/books/add")
	public String showAddBookForm(Model model) {
		model.addAttribute("book", new BookDTO());
		model.addAttribute("categories", categoryService.getAllCategories());
		model.addAttribute("publishers", publisherService.getAllPublishers());
		return "book/add";
	}

	// POST Add Book
	@PostMapping("/books/add")
	public String addBook(@ModelAttribute BookDTO book, RedirectAttributes redirectAttributes) {

		BookDTO savedBook = bookService.addBook(book);
		redirectAttributes.addAttribute("isbn", savedBook.getIsbn());
		redirectAttributes.addAttribute("msg", "Book added successfully!");
		return "redirect:/books/details";
	}

	// GET Show Update Form
	@GetMapping("/books/update")
	public String showUpdateForm(@RequestParam String isbn, Model model) {

		BookDetailsDTO book = bookService.getBookByIsbn(isbn);

		BookDTO bookForm = new BookDTO();
		bookForm.setIsbn(book.getIsbn());
		bookForm.setTitle(book.getTitle());
		bookForm.setDescription(book.getDescription());
		bookForm.setEdition(book.getEdition());

		model.addAttribute("book", bookForm);
		model.addAttribute("categories", categoryService.getAllCategories());
		model.addAttribute("publishers", publisherService.getAllPublishers());

		return "book/update";
	}

	// POST Update Book
	@PostMapping("/books/update")
	public String updateBook(@ModelAttribute BookDTO book, RedirectAttributes redirectAttributes) {

		bookService.updateBook(book.getIsbn(), book);
		redirectAttributes.addAttribute("isbn", book.getIsbn());
		redirectAttributes.addAttribute("msg", "Book updated successfully!");
		return "redirect:/books/details";
	}

	// GET Delete Form
	@GetMapping("/books/delete")
	public String showDeleteForm() {
		return "book/delete";
	}

	// POST Delete Book
	@PostMapping("/books/delete")
	public String deleteBook(@RequestParam String isbn, RedirectAttributes redirectAttributes) {

		bookService.deleteBook(isbn);
		redirectAttributes.addFlashAttribute("message", "Book '" + isbn + "' deleted successfully.");
		return "redirect:/books";
	}
}