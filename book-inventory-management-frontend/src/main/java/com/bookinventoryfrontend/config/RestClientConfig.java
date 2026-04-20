package com.bookinventoryfrontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Spring reads this value from application.properties
    // backend.base-url=http://localhost:8080
    @Value("${backend.base-url}")
    private String backendBaseUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(backendBaseUrl)   // All requests will start from this URL
                .build();
    }
}