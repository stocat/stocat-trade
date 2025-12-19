package com.stocat.tradeapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ApiClientConfig {
    @Value("${endpoints.asset-api}")
    private String assetApiUrl;

    @Bean
    public RestTemplate assetApiRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(assetApiUrl)
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }
}
