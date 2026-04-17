package com.bookinventory.author.controller;

import com.bookinventory.author.dto.AuthorDTO;
import com.bookinventory.author.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;
    @MockBean
    private com.bookinventory.user.util.JwtUtil jwtUtil;

    @Test
    void testGetAllAuthors() throws Exception {

        AuthorDTO dto = new AuthorDTO();
        dto.setAuthorID(1);
        dto.setFirstName("James");

        when(authorService.getAllAuthors()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/authors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
    }
}