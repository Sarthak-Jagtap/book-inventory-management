package com.bookinventory.book.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.book.entity.Book;
import com.bookinventory.book.repository.BookRepository;
import com.bookinventory.category.entity.Category;
import com.bookinventory.category.repository.CategoryRepository;
import com.bookinventory.publisher.entity.Publisher;
import com.bookinventory.publisher.repository.PublisherRepository;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	public Book addBook(Book book, int categoryId, int publisherId) {
		Publisher publisher = publisherRepository.findById(publisherId)
				.orElseThrow(() -> new RuntimeException("Publisher not found"));

		Category category = categoryRepository.findById(categoryId).orElse(null);

		book.setPublisher(publisher);
		book.setCategory(category);

		return bookRepository.save(book);
	}

	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	public Book getBookById(String isbn) {
		return bookRepository.findById(isbn).orElseThrow(() -> new RuntimeException("book Not Found"));
	}

	public Book updateBook(String isbn, Book updatedBood, int categoryId, int publisherId) {

		Book existingBook = bookRepository.findById(isbn).orElseThrow(() -> new RuntimeException("book not Found"));

		Publisher publisher = publisherRepository.findById(publisherId)
				.orElseThrow(() -> new RuntimeException("Publisher not found"));

		Category category = categoryRepository.findById(categoryId).orElse(null);

		existingBook.setPublisher(publisher);
		existingBook.setCategory(category);
		existingBook.setDescription(updatedBood.getDescription());
		existingBook.setEdition(updatedBood.getEdition());
		existingBook.setTitle(updatedBood.getTitle());

		return bookRepository.save(updatedBood);
	}

	public void deleteBookById(String isbn) {
		// Exception Handling Required
		bookRepository.deleteById(isbn);
	}
}
