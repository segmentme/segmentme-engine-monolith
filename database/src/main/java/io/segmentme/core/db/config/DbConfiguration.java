package io.segmentme.core.db.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = "io.segmentme.core")
@EnableConfigurationProperties
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
    public MappingMongoConverter mongoConverter(MongoDatabaseFactory mongoFactory, MongoMappingContext mongoMappingContext) throws Exception {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoFactory);
        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        mongoConverter.setMapKeyDotReplacement("#");
        mongoConverter.afterPropertiesSet();
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
                throw new RuntimeException("Unable to parse DbObject to JsonNode", e);
            }
        }
    }
}
