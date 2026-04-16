package com.bookinventory.book.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bookinventory.book.dto.BookDetailsResponseDTO;
import com.bookinventory.book.dto.BookRequestDTO;
import com.bookinventory.book.dto.BookResponseDTO;
import com.bookinventory.book.service.BookService;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.user.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookService;

	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private JwtUtil jwtUtil;
	
	// GET : Get all Books
	@Test
	void testGetAllBooks() throws Exception {
		BookResponseDTO dto = new BookResponseDTO();
		dto.setIsbn("123");
		dto.setTitle("Test Book");

		Page<BookResponseDTO> page =
		        new PageImpl<>(List.of(dto));

		when(bookService.getAllBooks(0, 10)).thenReturn(page);
		
		mockMvc.perform(get("/api/v1/books?page=0&size=10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content[0].title")
        .value("Test Book"));
	}
	
	// GET : Get book by ISBN : Success
	@Test
	void testGetBookByIsbn() throws Exception {

	    BookResponseDTO dto = new BookResponseDTO();
	    dto.setIsbn("123");
	    dto.setTitle("Test Book");

	    when(bookService.getBookById("123")).thenReturn(dto);

	    mockMvc.perform(get("/api/v1/books/123"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.data.title").value("Test Book"));
	}
	
	// GET : Get book by ISBN : Not Found
	@Test
	void testGetBookByIsbn_NotFound() throws Exception {

	    when(bookService.getBookById("123"))
	            .thenThrow(new ResourceNotFoundException("Book", "isbn", "123"));

	    mockMvc.perform(get("/api/v1/books/123"))
	            .andExpect(status().isNotFound());
	}
	
	// POST : Create Book
	@Test
	void testCreateBook() throws Exception {

	    BookRequestDTO request = new BookRequestDTO();
	    request.setIsbn("123");
	    request.setTitle("Test Book");
	    request.setCategoryId(1);
	    request.setPublisherId(1);

	    BookResponseDTO response = new BookResponseDTO();
	    response.setIsbn("123");
	    response.setTitle("Test Book");

	    when(bookService.createBook(any(BookRequestDTO.class)))
	            .thenReturn(response);

	    mockMvc.perform(post("/api/v1/store-owner/books")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isCreated())
	            .andExpect(jsonPath("$.data.title").value("Test Book"));
	}
	
	// PUT : Update Book : Success
	@Test
	void testUpdateBook() throws Exception {

	    BookRequestDTO request = new BookRequestDTO();
	    request.setIsbn("123");  // ✅ IMPORTANT
	    request.setTitle("Updated Book");
	    request.setCategoryId(1);
	    request.setPublisherId(1);

	    BookResponseDTO response = new BookResponseDTO();
	    response.setIsbn("123");
	    response.setTitle("Updated Book");

	    when(bookService.updateBook(eq("123"), any(BookRequestDTO.class)))
	            .thenReturn(response);

	    mockMvc.perform(put("/api/v1/store-owner/books/123")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.data.title").value("Updated Book"));
	}
	
	// PUT : Update Book : Not Found 
	@Test
	void testUpdateBook_NotFound() throws Exception {

	    BookRequestDTO request = new BookRequestDTO();
	    request.setIsbn("123");  // ✅ IMPORTANT
	    request.setTitle("Updated Book");
	    request.setCategoryId(1);
	    request.setPublisherId(1);

	    when(bookService.updateBook(eq("123"), any(BookRequestDTO.class)))
	            .thenThrow(new ResourceNotFoundException("Book", "isbn", "123"));

	    mockMvc.perform(put("/api/v1/store-owner/books/123")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isNotFound());
	}
	
	// DELETE  : delete book : Success
	@Test
	void testDeleteBook() throws Exception {

	    doNothing().when(bookService).deleteBook("123");

	    mockMvc.perform(delete("/api/v1/store-owner/books/123"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.message")
	            .value("Book deleted successfully"));
	}
	
	// DELETE : delete Book : Not Found
	@Test
	void testDeleteBook_NotFound() throws Exception {

	    doThrow(new ResourceNotFoundException("Book", "isbn", "123"))
	            .when(bookService).deleteBook("123");

	    mockMvc.perform(delete("/api/v1/store-owner/books/123"))
	            .andExpect(status().isNotFound());
	}
	
	// GET : Get books by Category
	@Test
	void testGetBooksByCategory() throws Exception {

	    BookResponseDTO dto = new BookResponseDTO();
	    dto.setTitle("Test Book");

	    Page<BookResponseDTO> page = new PageImpl<>(List.of(dto));

	    when(bookService.getBooksByCategory(1, 0, 10))
	            .thenReturn(page);

	    mockMvc.perform(get("/api/v1/books/category/1?page=0&size=10"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.data.content[0].title").value("Test Book"));
	}
	
	// GET : Get books by Publisher
	@Test
	void testGetBooksByPublisher() throws Exception {

	    BookResponseDTO dto = new BookResponseDTO();
	    dto.setTitle("Test Book");

	    Page<BookResponseDTO> page = new PageImpl<>(List.of(dto));

	    when(bookService.getBooksByPublisher(1, 0, 10))
	            .thenReturn(page);

	    mockMvc.perform(get("/api/v1/books/publisher/1?page=0&size=10"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.data.content[0].title").value("Test Book"));
	}
	
	// GET : Search Book
	@Test
	void testSearchBooks() throws Exception {

	    BookResponseDTO dto = new BookResponseDTO();
	    dto.setTitle("Test Book");

	    when(bookService.searchBooks("test", 1, 1))
	            .thenReturn(List.of(dto));

	    mockMvc.perform(get("/api/v1/books/search?title=test&categoryId=1&publisherId=1"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.data[0].title").value("Test Book"));
	}
	
	// GET : Get Book Details
	@Test
	void testGetBookDetails() throws Exception {

	    BookDetailsResponseDTO dto = new BookDetailsResponseDTO();
	    dto.setIsbn("123");
	    dto.setTitle("Test Book");

	    when(bookService.getBookDetails("123")).thenReturn(dto);

	    mockMvc.perform(get("/api/v1/books/123/details"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.data.title").value("Test Book"));
	}	
}