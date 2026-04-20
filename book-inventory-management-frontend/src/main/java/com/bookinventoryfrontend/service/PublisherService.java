package com.bookinventoryfrontend.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.bookinventoryfrontend.dto.ApiResponseDTO;
import com.bookinventoryfrontend.dto.PublisherDTO;
import com.bookinventoryfrontend.dto.PublisherRequestDTO;

@Service
public class PublisherService {

    private final RestClient restClient;

    public PublisherService(RestClient restClient) {
        this.restClient = restClient;
    }

    // GET ALL
    public List<PublisherDTO> getAllPublishers() {

        ApiResponseDTO<PublisherDTO[]> response = restClient.get()
                .uri("/api/v1/publishers")
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<PublisherDTO[]>>() {});

        return Arrays.asList(response.getData());
    }

    // GET BY ID
    public PublisherDTO getPublisherById(Integer id) {

        ApiResponseDTO<PublisherDTO> response = restClient.get()
                .uri("/api/v1/publishers/" + id)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<PublisherDTO>>() {});

        return response.getData();
    }

    // GET BY STATE
    public List<PublisherDTO> getPublishersByState(String stateCode) {

        ApiResponseDTO<PublisherDTO[]> response = restClient.get()
                .uri("/api/v1/publishers/state/" + stateCode)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<PublisherDTO[]>>() {});

        return Arrays.asList(response.getData());
    }

    // POST Create Publisher
    public PublisherDTO createPublisher(PublisherRequestDTO dto) {

        ApiResponseDTO<PublisherDTO> response = restClient.post()
                .uri("/api/v1/store-owner/publishers")
                .body(dto)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<PublisherDTO>>() {});

        return response.getData();
    }

    // UPDATE Update Publisher
    public PublisherDTO updatePublisher(Integer id, PublisherRequestDTO dto) {

        ApiResponseDTO<PublisherDTO> response = restClient.put()
                .uri("/api/v1/store-owner/publishers/" + id)
                .body(dto)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<PublisherDTO>>() {});

        return response.getData();
    }

    // DELETE Delete Publisher
    public void deletePublisher(Integer id) {

        restClient.delete()
                .uri("/api/v1/store-owner/publishers/" + id)
                .retrieve()
                .toBodilessEntity();
    }
}