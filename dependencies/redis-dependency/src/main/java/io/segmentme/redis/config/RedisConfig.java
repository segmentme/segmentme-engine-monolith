package io.segmentme.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.segmentme.redis.dto.RedisMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@EnableRedisRepositories(basePackages = "io.segmentme")
public abstract class RedisConfig {

    @Autowired
    protected RedisProperties redisProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    protected LettuceConnectionFactory redisConnectionFactory() {
        var config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        var factory = new LettuceConnectionFactory(config, LettuceClientConfiguration.builder().build());
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public ReactiveRedisMessageListenerContainer container(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisMessageListenerContainer(factory);
    }

    @Bean
    public MessagePublisher analysisTopicPublisher(RedisTemplate<String, RedisMessage> redisTemplate) {
        return new RedisMessagePublisher(redisTemplate);
    }

    @Bean
    protected RedisTemplate<String, RedisMessage> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        objectMapper.registerModule(module);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        var template = new RedisTemplate<String, RedisMessage>();
        template.setConnectionFactory(redisConnectionFactory);
        var jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jsonRedisSerializer.setObjectMapper(objectMapper);
        template.setKeySerializer(jsonRedisSerializer);
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashKeySerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        return template;
    }

    @Bean
    public ReactiveRedisOperations<String, Object> redisOperations(ReactiveRedisConnectionFactory factory) {

        var serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        var jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jsonRedisSerializer.setObjectMapper(objectMapper);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(jsonRedisSerializer);

        var context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
