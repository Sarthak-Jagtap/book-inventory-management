package com.bookinventory.publisher.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bookinventory.common.exception.ResourceNotFoundException;
import com.bookinventory.publisher.dto.PublisherRequestDTO;
import com.bookinventory.publisher.dto.PublisherResponseDTO;
import com.bookinventory.publisher.service.PublisherService;
import com.bookinventory.user.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PublisherController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublisherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PublisherService publisherService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtUtil jwtUtil;

	// GET - Get all publishers
	@Test
	void testGetAllPublishers() throws Exception {
		PublisherResponseDTO dto = new PublisherResponseDTO();
		dto.setPublisherId(1);
		dto.setName("ABC Publications");

		when(publisherService.getAllPublishers()).thenReturn(List.of(dto));

		mockMvc.perform(get("/api/v1/publishers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].name")
				.value("ABC Publications"))
				.andDo(print());
	}

	// GET - Get Publisher By Id
	@Test
	void testGetPublisherById() throws Exception {

		PublisherResponseDTO dto = new PublisherResponseDTO();
		dto.setPublisherId(1);
		dto.setName("ABC Publications");

		when(publisherService.getPublisherById(1)).thenReturn(dto);

		mockMvc.perform(get("/api/v1/publishers/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name")
				.value("ABC Publications"))
				.andDo(print());
	}
	
	// GET - Get Publisher by Id - Not Found
	@Test
	void testGetPublisherById_NotFound() throws Exception {

	    when(publisherService.getPublisherById(1))
	            .thenThrow(new ResourceNotFoundException("Publisher", "id", 1));

	    mockMvc.perform(get("/api/v1/publishers/1"))
	            .andExpect(status().isNotFound())
				.andDo(print());
	}
	
	@Test
	void testCreatePublisher() throws Exception {

	    PublisherRequestDTO request = new PublisherRequestDTO();
	    request.setPublisherId(1);   // ✅ REQUIRED now
	    request.setName("ABC Publications");
	    request.setCity("Pune");
	    request.setStateCode("MH");

	    PublisherResponseDTO response = new PublisherResponseDTO();
	    response.setPublisherId(1);
	    response.setName("ABC Publications");
	    response.setCity("Pune");
	    response.setStateCode("MH");

	    when(publisherService.createPublisher(any(PublisherRequestDTO.class)))
	            .thenReturn(response);

	    mockMvc.perform(post("/api/v1/store-owner/publishers")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isCreated())
	            .andExpect(jsonPath("$.data.publisherId").value(1))   // ✅ added
	            .andExpect(jsonPath("$.data.name").value("ABC Publications"))
	            .andExpect(jsonPath("$.data.city").value("Pune"))     // ✅ added
	            .andExpect(jsonPath("$.data.stateCode").value("MH"))  // ✅ added
	            .andDo(print());

	    verify(publisherService).createPublisher(any(PublisherRequestDTO.class));
	}
	
	// PUT - Update Publisher
	@Test
	void testUpdatePublisher() throws Exception {

	    PublisherRequestDTO request = new PublisherRequestDTO();
	    request.setPublisherId(1);   // ✅ REQUIRED now
	    request.setName("Updated Publisher");
	    request.setCity("Mumbai");
	    request.setStateCode("MH");

	    PublisherResponseDTO response = new PublisherResponseDTO();
	    response.setPublisherId(1);
	    response.setName("Updated Publisher");
	    response.setCity("Mumbai");
	    response.setStateCode("MH");

	    when(publisherService.updatePublisher(eq(1), any(PublisherRequestDTO.class)))
	            .thenReturn(response);

	    mockMvc.perform(put("/api/v1/store-owner/publishers/1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.data.publisherId").value(1))   // ✅ added
	            .andExpect(jsonPath("$.data.name").value("Updated Publisher"))
	            .andExpect(jsonPath("$.data.city").value("Mumbai"))   // ✅ added
	            .andExpect(jsonPath("$.data.stateCode").value("MH"))  // ✅ added
	            .andDo(print());

	    verify(publisherService).updatePublisher(eq(1), any(PublisherRequestDTO.class));
	}
	
	// DELETE - Delete Publisher
	@Test
	void testDeletePublisher() throws Exception {

	    doNothing().when(publisherService).deletePublisher(1);

	    mockMvc.perform(delete("/api/v1/store-owner/publishers/1"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.message")
	            .value("Publisher deleted successfully"))
				.andDo(print());
	}

	// GET - Get Publisher by State
	@Test
	void testGetPublishersByState() throws Exception {

	    PublisherResponseDTO dto = new PublisherResponseDTO();
	    dto.setPublisherId(1);
	    dto.setName("ABC Publications");

	    when(publisherService.getPublishersByState("MH"))
	            .thenReturn(List.of(dto));

	    mockMvc.perform(get("/api/v1/publishers/state/MH"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.data[0].name")
	            .value("ABC Publications"))
				.andDo(print());
	}
}