package com.bookinventory.author.service;


import org.springframework.stereotype.Service;


import com.bookinventory.author.dto.AuthorDTO;
import com.bookinventory.author.entity.Author;
import com.bookinventory.author.mapper.AuthorMapper;
import com.bookinventory.author.repository.AuthorRepository;
import com.bookinventory.book.entity.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
	
    }

    // CREATE AUTHOR
    public AuthorDTO createAuthor(AuthorDTO dto) {

        Author author = AuthorMapper.toEntity(dto);

        Author savedAuthor = authorRepository.save(author);

        return AuthorMapper.toDTO(savedAuthor);
    }

    // GET ALL AUTHORS
    public List<AuthorDTO> getAllAuthors() {

        return authorRepository.findAll()
                .stream()
                .map(AuthorMapper::toDTO)
                .collect(Collectors.toList());
    }

    // GET AUTHOR BY ID
    public AuthorDTO getAuthorById(Integer id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        return AuthorMapper.toDTO(author);
    }

    // UPDATE AUTHOR
    public AuthorDTO updateAuthor(Integer id, AuthorDTO dto) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        author.setPhoto(dto.getPhoto());

        Author updated = authorRepository.save(author);

        return AuthorMapper.toDTO(updated);
    }

    // DELETE AUTHOR
    public void deleteAuthor(Integer id) {

        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found");
        }

        authorRepository.deleteById(id);
    }
    
    //SEARCH AUTHOR
    public List<AuthorDTO> searchAuthors(String name) {

        List<Author> authors =
                authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);

        return authors.stream()
                .map(AuthorMapper::toDTO)
                .toList();
    }
    
    //AUTHOR'S BOOKS
    public List<Book> getBooksByAuthor(Integer authorId) {

        return authorRepository.findBooksByAuthorId(authorId);
    }
    
    //NO.S OF BOOKS OF AUTHOR
    public Map<String, Object> getAuthorStats(Integer authorId) {

        Long count = authorRepository.countBooksByAuthor(authorId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("authorId", authorId);
        stats.put("booksWritten", count);

        return stats;
    }
}
