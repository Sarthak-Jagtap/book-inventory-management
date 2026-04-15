package com.bookinventory.book.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bookinventory.author.entity.BookAuthor;
import com.bookinventory.author.repository.AuthorRepository;
import com.bookinventory.author.repository.BookAuthorRepository;
import com.bookinventory.book.dto.BookDetailsResponseDTO;
import com.bookinventory.book.dto.BookRequestDTO;
import com.bookinventory.book.dto.BookResponseDTO;
import com.bookinventory.book.entity.Book;
import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.bookreview.entity.BookReview;
import com.bookinventory.bookreview.repository.BookReviewRepository;
import com.bookinventory.category.entity.Category;
import com.bookinventory.category.repository.CategoryRepository;
import com.bookinventory.publisher.entity.Publisher;
import com.bookinventory.publisher.repository.PublisherRepository;
import com.bookinventory.common.exception.ResourceNotFoundException;

@Service
public class BookService {

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookAuthorRepository bookAuthorRepository;

	@Autowired
	private BookReviewRepository bookReviewRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	public BookResponseDTO createBook(BookRequestDTO dto) {

		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

		Publisher publisher = publisherRepository.findById(dto.getPublisherId())
				.orElseThrow(() -> new ResourceNotFoundException("Publisher", "id", dto.getPublisherId()));

		Book book = convertToEntity(dto, category, publisher);

		Book savedBook = bookRepository.save(book);

		return convertToDTO(savedBook);
	}

	public Page<BookResponseDTO> getAllBooks(int page, int size) {

		Sort sort = Sort.by("title").ascending();

		Pageable pageable = PageRequest.of(page, size, sort);

		Page<Book> bookPage = bookRepository.findAll(pageable);

		return bookPage.map(this::convertToDTO);
	}

	public BookResponseDTO getBookById(String isbn) {

		Book book = bookRepository.findById(isbn)
				.orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));

		return convertToDTO(book);
	}

	public BookResponseDTO updateBook(String isbn, BookRequestDTO dto) {

		Book existingBook = bookRepository.findById(isbn)
				.orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));

		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

		Publisher publisher = publisherRepository.findById(dto.getPublisherId())
				.orElseThrow(() -> new ResourceNotFoundException("Publisher", "id", dto.getPublisherId()));

		existingBook.setTitle(dto.getTitle());
		existingBook.setDescription(dto.getDescription());
		existingBook.setEdition(dto.getEdition());
		existingBook.setCategory(category);
		existingBook.setPublisher(publisher);

		Book updatedBook = bookRepository.save(existingBook);

		return convertToDTO(updatedBook);
	}

	public void deleteBook(String isbn) {

		Book book = bookRepository.findById(isbn)
				.orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));

		bookRepository.delete(book);
	}

	public Page<BookResponseDTO> getBooksByCategory(Integer categoryId, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());

		Page<Book> bookPage = bookRepository.findByCategory_CatId(categoryId, pageable);

		return bookPage.map(this::convertToDTO);
	}

	private Book convertToEntity(BookRequestDTO dto, Category category, Publisher publisher) {
		Book book = new Book();
		book.setIsbn(dto.getIsbn());
		book.setTitle(dto.getTitle());
		book.setDescription(dto.getDescription());
		book.setEdition(dto.getEdition());
		book.setCategory(category);
		book.setPublisher(publisher);
		return book;
	}

	private BookResponseDTO convertToDTO(Book book) {
		BookResponseDTO dto = new BookResponseDTO();
		dto.setIsbn(book.getIsbn());
		dto.setTitle(book.getTitle());
		dto.setDescription(book.getDescription());
		dto.setEdition(book.getEdition());
		dto.setCategoryId(book.getCategory().getCatId());
		dto.setPublisherId(book.getPublisher().getPublisherId());
		return dto;
	}

	public Page<BookResponseDTO> getBooksByPublisher(Integer publisherId, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());

		Page<Book> bookPage = bookRepository.findByPublisher_PublisherId(publisherId, pageable);

		return bookPage.map(this::convertToDTO);
	}

	public List<BookResponseDTO> searchBooks(String title, Integer categoryId, Integer publisherId) {

		List<Book> books = bookRepository.searchBooks(title, categoryId, publisherId);

		return books.stream().map(this::convertToDTO).toList();
	}

	public BookDetailsResponseDTO getBookDetails(String isbn) {

		Book book = bookRepository.findById(isbn)
				.orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));

		List<BookAuthor> bookAuthors = bookAuthorRepository.findByIdISBN(isbn);

		List<String> authors = bookAuthors.stream()
				.map(ba -> authorRepository.findById(ba.getId().getAuthorID()).orElse(null))
				.filter(author -> author != null).map(author -> author.getFirstName() + " " + author.getLastName())
				.toList();

		List<BookReview> reviews = bookReviewRepository.findByBookIsbn(isbn);

		double avgRating = 0.0;
		int totalReviews = reviews.size();

		if (!reviews.isEmpty()) {
			avgRating = reviews.stream().mapToInt(BookReview::getRating).average().orElse(0.0);
		}

		BookDetailsResponseDTO dto = new BookDetailsResponseDTO();

		dto.setIsbn(book.getIsbn());
		dto.setTitle(book.getTitle());
		dto.setDescription(book.getDescription());
		dto.setEdition(book.getEdition());

		dto.setCategoryName(book.getCategory().getCatDescription());
		dto.setPublisherName(book.getPublisher().getName());

		dto.setAuthors(authors);
		dto.setAverageRating(avgRating);
		dto.setTotalReviews(totalReviews);

		return dto;
	}

}