package io.segmentme.core.db.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
@EnableAutoConfiguration
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
    private static final class JsonNodeToDocumentConverter implements Converter<JsonNode, Document> {

        public Document convert(JsonNode source) {
            return Optional.ofNullable(source).map(JsonNode::toString).map(Document::parse).orElse(null);
        }
    }

    @ReadingConverter
    private final class DocumentToJsonNodeConverter implements Converter<Document, JsonNode> {

        public JsonNode convert(Document source) {
            return Optional.ofNullable(source).map(Document::toJson).map(this::readValue).orElse(null);
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
