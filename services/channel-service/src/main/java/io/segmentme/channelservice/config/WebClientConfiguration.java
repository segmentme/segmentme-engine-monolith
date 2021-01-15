package io.segmentme.channelservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    @Value("${segmentme.application.analysis-service.url}")
    private String analysisServiceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(analysisServiceUrl).build();
    }
}
