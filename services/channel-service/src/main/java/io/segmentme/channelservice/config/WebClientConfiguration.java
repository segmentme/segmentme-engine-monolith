package io.segmentme.channelservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    @Bean
    public ThreadPoolTaskExecutor channelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("channel-executor-");
        return executor;
    }
}
