package com.bookinventoryfrontend.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.bookinventoryfrontend.dto.ApiResponseDTO;
import com.bookinventoryfrontend.dto.StateDTO;

@Service
public class StateService {

    private final RestClient restClient;

    public StateService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<StateDTO> getAllStates() {

    	ApiResponseDTO<StateDTO[]> response = restClient.get()
    	        .uri("/api/v1/states")
    	        .retrieve()
    	        .body(new ParameterizedTypeReference<ApiResponseDTO<StateDTO[]>>() {});

    	return Arrays.asList(response.getData());
    }
    
    public StateDTO getStateByStatecode(String stateCode) {

        ApiResponseDTO<StateDTO> response = restClient.get()
                .uri("/api/v1/states/" + stateCode)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponseDTO<StateDTO>>() {});

        return response.getData();
    }
}	