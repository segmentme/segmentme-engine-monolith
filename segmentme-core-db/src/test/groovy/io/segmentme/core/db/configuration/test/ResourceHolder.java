package io.segmentme.core.db.configuration.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@TestConfiguration
@Data
public class ResourceHolder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("classpath:validJsonPayload")
    private Resource validJsonPayloadConfiguration;

    @Value("classpath:invalidJsonPayload")
    private Resource invalidJsonPayloadConfiguration;

    public void init() {
        validJsonPayloadConfiguration = new ClassPathResource("validJsonPayload");
        invalidJsonPayloadConfiguration = new ClassPathResource("invalidJsonPayload");
    }

    @SneakyThrows
    public JsonNode getValidJsonPayloadConfiguration() {
        return OBJECT_MAPPER.readValue(validJsonPayloadConfiguration.getInputStream(), JsonNode.class);
    }

    @SneakyThrows
    public JsonNode getInvalidJsonPayloadConfiguration() {
        return OBJECT_MAPPER.readValue(invalidJsonPayloadConfiguration.getInputStream(), JsonNode.class);
    }
}
