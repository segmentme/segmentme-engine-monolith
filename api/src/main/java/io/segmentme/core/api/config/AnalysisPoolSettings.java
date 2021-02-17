package io.segmentme.core.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ConfigurationProperties("segmentme.application.analysis.pool")
@Configuration
@Data
public class AnalysisPoolSettings {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueSize;
    private String threadNamePrefix;

    @Bean
    public ThreadPoolTaskExecutor analysisThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(this.corePoolSize);
        executor.setMaxPoolSize(this.maxPoolSize);
        executor.setQueueCapacity(this.queueSize);
        executor.setThreadNamePrefix(this.threadNamePrefix);
        return executor;
    }
}
