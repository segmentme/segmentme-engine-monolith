package io.segmentme.core.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringCoreDataApplication {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
