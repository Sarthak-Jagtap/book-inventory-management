package com.bookinventory.author.service;

import org.springframework.stereotype.Service;

import com.bookinventory.author.dto.AuthorDTO;
import com.bookinventory.author.dto.BookAuthorDTO;
import com.bookinventory.author.entity.Author;
import com.bookinventory.author.entity.BookAuthor;
import com.bookinventory.author.entity.BookAuthorId;
import com.bookinventory.author.mapper.AuthorMapper;
import com.bookinventory.author.repository.AuthorRepository;
import com.bookinventory.author.repository.BookAuthorRepository;

import java.util.List;

@Service
public class BookAuthorService {

    private final BookAuthorRepository repository;
     private final AuthorRepository authorRepository;

    public BookAuthorService(BookAuthorRepository repository , AuthorRepository authorRepository) {
        this.repository = repository;
		this.authorRepository = authorRepository;
    }

    // ASSIGN AUTHOR TO BOOK
    public BookAuthor assignAuthorToBook(BookAuthorDTO dto) {

        BookAuthorId id = new BookAuthorId();
        id.setISBN(dto.getIsbn());
        id.setAuthorID(dto.getAuthorID());

        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.setId(id);
        bookAuthor.setPrimaryAuthor(dto.getPrimaryAuthor());

        return repository.save(bookAuthor);
    }

    // GET AUTHORS OF BOOK
    public List<BookAuthor> getAuthorsByBook(String isbn) {

        return repository.findByIdISBN(isbn);
    }

    // GET BOOKS OF AUTHOR
    public List<BookAuthor> getBooksByAuthor(Integer authorId) {

        return repository.findByIdAuthorID(authorId);
    }

    // REMOVE AUTHOR FROM BOOK
    public void removeAuthorFromBook(String isbn, Integer authorId) {

        BookAuthorId id = new BookAuthorId();
        id.setISBN(isbn);
        id.setAuthorID(authorId);

        repository.deleteById(id);
    }
    
    public BookAuthor updateBookAuthor(String isbn, Integer authorId, BookAuthorDTO dto) {

        BookAuthorId id = new BookAuthorId();
        id.setISBN(isbn);
        id.setAuthorID(authorId);

        BookAuthor existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "BookAuthor mapping not found for ISBN: " + isbn + " and AuthorID: " + authorId));

        // update fields
        existing.setPrimaryAuthor(dto.getPrimaryAuthor());

        return repository.save(existing);
    }
    
    public AuthorDTO getPrimaryAuthor(String isbn) {

        BookAuthor mapping = repository
                .findByIdISBNAndPrimaryAuthor(isbn, "Y")
                .orElseThrow(() -> new RuntimeException("Primary author not found"));

        Integer authorId = mapping.getId().getAuthorID();

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        return AuthorMapper.toDTO(author);
    }

}
