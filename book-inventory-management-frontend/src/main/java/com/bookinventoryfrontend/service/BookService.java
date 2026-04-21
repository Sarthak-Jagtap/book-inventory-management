package com.bookinventoryfrontend.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.bookinventoryfrontend.dto.ApiResponseDTO;
import com.bookinventoryfrontend.dto.BookDTO;
import com.bookinventoryfrontend.dto.BookDetailsDTO;
import com.bookinventoryfrontend.dto.PageResponseDTO;

@Service
public class BookService {

	private final RestClient restClient;

	public BookService(RestClient restClient) {
		this.restClient = restClient;
	}

	public PageResponseDTO<BookDTO> getAllBooks(int page, int size) {

	    String uri = "/api/v1/books?page=" + page + "&size=" + size;

	    ApiResponseDTO<PageResponseDTO<BookDTO>> response = restClient.get()
	            .uri(uri)
	            .retrieve()
	            .body(new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<BookDTO>>>() {});

	    return response.getData();
	}
	
	public BookDetailsDTO getBookByIsbn(String isbn) {

	    ApiResponseDTO<BookDetailsDTO> response = restClient.get()
	            .uri("/api/v1/books/" + isbn + "/details")
	            .retrieve()
	            .body(new ParameterizedTypeReference<ApiResponseDTO<BookDetailsDTO>>() {});

	    return response.getData();
	}
	
	public List<BookDTO> searchBooks(String title, Integer categoryId, Integer publisherId) {

	    String uri = "/api/v1/books/search?title=" + (title != null ? title : "")
	            + "&categoryId=" + (categoryId != null ? categoryId : "")
	            + "&publisherId=" + (publisherId != null ? publisherId : "");

	    ApiResponseDTO<BookDTO[]> response = restClient.get()
	            .uri(uri)
	            .retrieve()
	            .body(new ParameterizedTypeReference<ApiResponseDTO<BookDTO[]>>() {});

	    return Arrays.asList(response.getData());
	}
	
	public PageResponseDTO<BookDTO> getBooksByCategory(Integer categoryId, int page, int size) {

	    String uri = "/api/v1/books/category/" + categoryId +
	            "?page=" + page + "&size=" + size;

	    ApiResponseDTO<PageResponseDTO<BookDTO>> response = restClient.get()
	            .uri(uri)
	            .retrieve()
	            .body(new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<BookDTO>>>() {});

	    return response.getData();
	}
	
	public PageResponseDTO<BookDTO> getBooksByPublisher(Integer publisherId, int page, int size) {

	    String uri = "/api/v1/books/publisher/" + publisherId +
	            "?page=" + page + "&size=" + size;

	    ApiResponseDTO<PageResponseDTO<BookDTO>> response = restClient.get()
	            .uri(uri)
	            .retrieve()
	            .body(new ParameterizedTypeReference<ApiResponseDTO<PageResponseDTO<BookDTO>>>() {});

	    return response.getData();
	}
	
	public BookDTO addBook(BookDTO dto) {

	    ApiResponseDTO<BookDTO> response = restClient.post()
	            .uri("/api/v1/store-owner/books")
	            .body(dto)
	            .retrieve()
	            .body(new ParameterizedTypeReference<ApiResponseDTO<BookDTO>>() {});

	    return response.getData();
	}
	
	public BookDTO updateBook(String isbn, BookDTO dto) {

	    ApiResponseDTO<BookDTO> response = restClient.put()
	            .uri("/api/v1/store-owner/books/" + isbn)
	            .body(dto)
	            .retrieve()
	            .body(new ParameterizedTypeReference<ApiResponseDTO<BookDTO>>() {});

	    return response.getData();
	}
	
	public void deleteBook(String isbn) {

        restClient.delete()
                .uri("/api/v1/store-owner/books/" + isbn)
                .retrieve()
                .toBodilessEntity();
    }
}