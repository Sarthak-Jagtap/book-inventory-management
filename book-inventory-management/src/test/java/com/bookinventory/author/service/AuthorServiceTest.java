package com.bookinventory.author.service;

import com.bookinventory.author.dto.AuthorDTO;
import com.bookinventory.author.entity.Author;
import com.bookinventory.author.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void testCreateAuthor() {

        AuthorDTO dto = new AuthorDTO();
        dto.setFirstName("James");
        dto.setLastName("Gosling");

        Author savedAuthor = new Author();
        savedAuthor.setAuthorID(1);
        savedAuthor.setFirstName("James");
        savedAuthor.setLastName("Gosling");

        when(authorRepository.save(any())).thenReturn(savedAuthor);

        AuthorDTO result = authorService.createAuthor(dto);

        assertEquals("James", result.getFirstName());
        verify(authorRepository, times(1)).save(any());
    }

    @Test
    void testGetAllAuthors() {

        Author author = new Author();
        author.setAuthorID(1);
        author.setFirstName("James");

        when(authorRepository.findAll()).thenReturn(List.of(author));

        List<AuthorDTO> result = authorService.getAllAuthors();

        assertEquals(1, result.size());
        verify(authorRepository).findAll();
    }

    @Test
    void testGetAuthorById() {

        Author author = new Author();
        author.setAuthorID(1);
        author.setFirstName("James");

        when(authorRepository.findById(1)).thenReturn(Optional.of(author));

        AuthorDTO result = authorService.getAuthorById(1);

        assertEquals("James", result.getFirstName());
    }

    @Test
    void testDeleteAuthor() {

        when(authorRepository.existsById(1)).thenReturn(true);

        authorService.deleteAuthor(1);

        verify(authorRepository).deleteById(1);
    }

    @Test
    void testSearchAuthors() {

        Author author = new Author();
        author.setAuthorID(1);
        author.setFirstName("James");

        when(authorRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("jam","jam"))
                .thenReturn(List.of(author));

        List<AuthorDTO> result = authorService.searchAuthors("jam");

        assertEquals(1, result.size());
    }

}