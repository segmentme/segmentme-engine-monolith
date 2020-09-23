package io.segmentme.core.db.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DbConfiguration {

    private final ObjectMapper objectMapper;

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new JsonNodeToDocumentConverter());
        converters.add(new DocumentToJsonNodeConverter());
        return new MongoCustomConversions(converters);
    }

    @WritingConverter
    private static final class JsonNodeToDocumentConverter implements Converter<JsonNode, String> {

        public String convert(JsonNode source) {
            return Optional.ofNullable(source).map(JsonNode::toString).orElse(null);
        }
    }

    @ReadingConverter
    private final class DocumentToJsonNodeConverter implements Converter<String, JsonNode> {

        public JsonNode convert(String source) {
            return Optional.ofNullable(source).map(this::readValue).orElse(null);
        }

        private JsonNode readValue(String value) {
            try {
                return objectMapper.readTree(value);
            } catch (IOException e) {
                throw new RuntimeException("Unable to parse DbObject to JsonNode", e);
            }
        }
    }
}
