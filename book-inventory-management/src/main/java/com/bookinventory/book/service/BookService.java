package com.bookinventory.book.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.book.dto.BookRequestDTO;
import com.bookinventory.book.dto.BookResponseDTO;
import com.bookinventory.book.entity.Book;
import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.category.entity.Category;
import com.bookinventory.category.repository.CategoryRepository;
import com.bookinventory.publisher.entity.Publisher;
import com.bookinventory.publisher.repository.PublisherRepository;
import com.bookinventory.user.common.exception.ResourceNotFoundException;

@Service
public class BookService {

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

	public List<BookResponseDTO> getAllBooks() {

		return bookRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
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
}