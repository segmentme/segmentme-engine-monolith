package io.segmentme.db.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.util.*;

@Order(1)
@Configuration
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = "io.segmentme.db")
@EnableConfigurationProperties
@EnableMongoAuditing
public class DbConfiguration {

    private final ObjectMapper objectMapper;

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new JsonNodeToDocumentConverter());
        converters.add(new DocumentToJsonNodeConverter());
        return new MongoCustomConversions(converters);
    }

    @Bean
    public MappingMongoConverter mongoConverter(MongoDatabaseFactory mongoFactory, MongoMappingContext mongoMappingContext, MongoCustomConversions customConversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoFactory);
        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        mongoConverter.setMapKeyDotReplacement("#");
        mongoConverter.afterPropertiesSet();
        mongoConverter.setCustomConversions(customConversions);
        return mongoConverter;
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
                throw new IllegalArgumentException("Unable to parse DbObject to JsonNode", e);
            }
        }
    }
}
