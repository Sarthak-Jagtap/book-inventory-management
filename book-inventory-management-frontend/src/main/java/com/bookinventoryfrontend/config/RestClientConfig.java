package com.bookinventoryfrontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder,
                                 @Value("${backend.base-url}") String backendBaseUrl) {
        return builder
                .baseUrl(backendBaseUrl)
                .build();
    }
}