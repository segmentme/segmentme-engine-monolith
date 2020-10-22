package io.segmentme.core.service.configuration.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

@TestConfiguration
@Data
public class ResourceHolder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("classpath:validJsonPayload")
    private Resource validJsonPayloadConfiguration;

    @Value("classpath:invalidJsonPayload")
    private Resource invalidJsonPayloadConfiguration;

    @Value("classpath:rules/rules.json")
    protected Resource ruleSchema;

    public void init() {
        validJsonPayloadConfiguration = new ClassPathResource("validJsonPayload");
        invalidJsonPayloadConfiguration = new ClassPathResource("invalidJsonPayload");
        ruleSchema = new ClassPathResource("rules/rules.json");
    }

    @SneakyThrows
    public JsonNode getValidJsonPayloadConfiguration() {
        return OBJECT_MAPPER.readValue(validJsonPayloadConfiguration.getInputStream(), JsonNode.class);
    }

    @SneakyThrows
    public List<Segment> getSegments() {
        return OBJECT_MAPPER.readValue(ruleSchema.getInputStream(), new TypeReference<>() {});
    }


    @SneakyThrows
    public List<SegmentDto> getSegmentsDto() {
        return OBJECT_MAPPER.readValue(ruleSchema.getInputStream(), new TypeReference<>() {});
    }

    @SneakyThrows
    public JsonNode getInvalidJsonPayloadConfiguration() {
        return OBJECT_MAPPER.readValue(invalidJsonPayloadConfiguration.getInputStream(), JsonNode.class);
    }
}
