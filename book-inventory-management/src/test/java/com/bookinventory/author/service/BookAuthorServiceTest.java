package com.bookinventory.author.service;

import com.bookinventory.author.dto.AuthorDTO;
import com.bookinventory.author.dto.BookAuthorDTO;
import com.bookinventory.author.entity.Author;
import com.bookinventory.author.entity.BookAuthor;
import com.bookinventory.author.entity.BookAuthorId;
import com.bookinventory.author.repository.AuthorRepository;
import com.bookinventory.author.repository.BookAuthorRepository;

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
class BookAuthorServiceTest {

    @Mock
    private BookAuthorRepository repository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookAuthorService service;

    @Test
    void testAssignAuthorToBook() {

        BookAuthorDTO dto = new BookAuthorDTO();
        dto.setIsbn("123");
        dto.setAuthorID(1);
        dto.setPrimaryAuthor("Y");

        BookAuthor saved = new BookAuthor();

        when(repository.save(any())).thenReturn(saved);

        BookAuthor result = service.assignAuthorToBook(dto);

        assertNotNull(result);
        verify(repository).save(any());
    }

    @Test
    void testGetAuthorsByBook() {

        when(repository.findByIdISBN("123"))
                .thenReturn(List.of(new BookAuthor()));

        List<BookAuthor> result = service.getAuthorsByBook("123");

        assertEquals(1, result.size());
    }

    @Test
    void testGetBooksByAuthor() {

        when(repository.findByIdAuthorID(1))
                .thenReturn(List.of(new BookAuthor()));

        List<BookAuthor> result = service.getBooksByAuthor(1);

        assertEquals(1, result.size());
    }

    @Test
    void testRemoveAuthorFromBook() {

        service.removeAuthorFromBook("123", 1);

        verify(repository).deleteById(any());
    }

    @Test
    void testUpdateBookAuthor() {

        BookAuthorId id = new BookAuthorId();
        id.setISBN("123");
        id.setAuthorID(1);

        BookAuthor existing = new BookAuthor();
        existing.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(existing);

        BookAuthorDTO dto = new BookAuthorDTO();
        dto.setPrimaryAuthor("N");

        BookAuthor result = service.updateBookAuthor("123", 1, dto);

        assertEquals("N", result.getPrimaryAuthor());
    }

    @Test
    void testGetPrimaryAuthor() {

        BookAuthorId id = new BookAuthorId();
        id.setISBN("123");
        id.setAuthorID(1);

        BookAuthor mapping = new BookAuthor();
        mapping.setId(id);

        Author author = new Author();
        author.setAuthorID(1);
        author.setFirstName("James");

        when(repository.findByIdISBNAndPrimaryAuthor("123","Y"))
                .thenReturn(Optional.of(mapping));

        when(authorRepository.findById(1))
                .thenReturn(Optional.of(author));

        AuthorDTO result = service.getPrimaryAuthor("123");

        assertEquals("James", result.getFirstName());
    }

}