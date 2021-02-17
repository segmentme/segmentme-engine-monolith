package io.segmentme.core.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Data
@Configuration
@ConfigurationProperties("segmentme.application.async.pool")
public class AsyncExecutorProperties {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueSize;
    private String threadNamePrefix;

    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueSize);
        executor.setThreadNamePrefix(threadNamePrefix);
        return executor;
    }
}
