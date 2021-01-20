package io.segmentme.core.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.segmentme.redis.config.*;
import io.segmentme.redis.dto.RedisMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class AnalysisRedisConfig extends RedisConfig {

    @Bean
    protected RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        return container;
    }

    @Bean
    public MessagePublisher analysisTopicPublisher(LettuceConnectionFactory redisConnectionFactory) {
        return new RedisMessagePublisher(analysisTopic(), redisTemplate(redisConnectionFactory));
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
    public ChannelTopic analysisTopic() {
        return new ChannelTopic("analysisChannelTopic");
    }
}
