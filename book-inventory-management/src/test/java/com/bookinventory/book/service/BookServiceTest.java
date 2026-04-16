package com.bookinventory.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.bookinventory.book.dto.BookRequestDTO;
import com.bookinventory.book.dto.BookResponseDTO;
import com.bookinventory.book.entity.Book;
import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.category.entity.Category;
import com.bookinventory.category.repository.CategoryRepository;
import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.publisher.entity.Publisher;
import com.bookinventory.publisher.repository.PublisherRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private PublisherRepository publisherRepository;

	@InjectMocks
	private BookService bookService;

	private Book book;
	private Category category;
	private Publisher publisher;
	private BookRequestDTO requestDTO;

	@BeforeEach
	void setUp() {

		category = new Category();
		category.setCatId(1);
		category.setCatDescription("Fiction");

		publisher = new Publisher();
		publisher.setPublisherId(1);
		publisher.setName("ABC Publications");

		book = new Book();
		book.setIsbn("123");
		book.setTitle("Test Book");
		book.setCategory(category);
		book.setPublisher(publisher);

		requestDTO = new BookRequestDTO();
		requestDTO.setIsbn("123");
		requestDTO.setTitle("Test Book");
		requestDTO.setCategoryId(1);
		requestDTO.setPublisherId(1);
	}

	// Create Book - Success
	@Test
	void testCreateBook_Success() {

		when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

		when(publisherRepository.findById(1)).thenReturn(Optional.of(publisher));

		when(bookRepository.save(any(Book.class))).thenReturn(book);

		BookResponseDTO result = bookService.createBook(requestDTO);

		assertNotNull(result);
		assertEquals("123", result.getIsbn());

		verify(categoryRepository).findById(1);
		verify(publisherRepository).findById(1);
		verify(bookRepository).save(any(Book.class));
	}

	// Create Book - Category Not Found
	@Test
	void testCreateBook_CategoryNotFound() {

		when(categoryRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.createBook(requestDTO);
		});
	}

	// Create Book - Publisher Not Found
	@Test
	void testCreateBook_PublisherNotFound() {

		when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

		when(publisherRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.createBook(requestDTO);
		});
	}

	// Get Book By Id : Success
	@Test
	void testGetBookById() {

		when(bookRepository.findById("123")).thenReturn(Optional.of(book));

		BookResponseDTO result = bookService.getBookById("123");

		assertEquals("Test Book", result.getTitle());
	}

	// Get Book By Id : Not Found
	@Test
	void testGetBookById_NotFound() {

		when(bookRepository.findById("123")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.getBookById("123");
		});
	}

	// Delete Book : Success
	@Test
	void testDeleteBook() {

		when(bookRepository.findById("123")).thenReturn(Optional.of(book));

		bookService.deleteBook("123");

		verify(bookRepository).delete(book);
	}

	// Delete Book : Not Found
	@Test
	void testDeleteBook_NotFound() {

		when(bookRepository.findById("123")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.deleteBook("123");
		});
	}

	// Pagination
	@Test
	void testGetAllBooks() {

		Page<Book> page = new PageImpl<>(List.of(book));

		when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);

		Page<BookResponseDTO> result = bookService.getAllBooks(0, 10);

		assertEquals(1, result.getContent().size());
	}

	// Get Book By Category
	@Test
	void testGetBooksByCategory() {

		Page<Book> page = new PageImpl<>(List.of(book));

		when(bookRepository.findByCategory_CatId(eq(1), any(Pageable.class))).thenReturn(page);

		Page<BookResponseDTO> result = bookService.getBooksByCategory(1, 0, 10);

		assertEquals(1, result.getContent().size());
	}

	// Get Book by Publisher
	@Test
	void testGetBooksByPublisher() {

		Page<Book> page = new PageImpl<>(List.of(book));

		when(bookRepository.findByPublisher_PublisherId(eq(1), any(Pageable.class))).thenReturn(page);

		Page<BookResponseDTO> result = bookService.getBooksByPublisher(1, 0, 10);

		assertEquals(1, result.getContent().size());
	}

	// Search Book
	@Test
	void testSearchBooks() {

		when(bookRepository.searchBooks("test", 1, 1)).thenReturn(List.of(book));

		List<BookResponseDTO> result = bookService.searchBooks("test", 1, 1);

		assertEquals(1, result.size());
	}
}