package com.bookinventory.author.controller;

import com.bookinventory.author.service.BookAuthorService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookAuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookAuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookAuthorService service;
    
    @MockBean
    private com.bookinventory.user.util.JwtUtil jwtUtil;

    @Test
    void testGetAuthorsByBook() throws Exception {

        when(service.getAuthorsByBook("123"))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/books/123/authors"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBooksByAuthor() throws Exception {

        when(service.getBooksByAuthor(1))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/books/author/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPrimaryAuthor() throws Exception {

        when(service.getPrimaryAuthor("123"))
                .thenReturn(new com.bookinventory.author.dto.AuthorDTO());

        mockMvc.perform(get("/api/v1/books/123/primary-author"))
                .andExpect(status().isOk());
    }

}