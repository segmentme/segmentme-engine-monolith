package io.segmentme.redis.config;

import io.segmentme.redis.dto.RedisMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;

@EnableRedisRepositories(basePackages = "io.segmentme")
public abstract class RedisConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    protected RedisTemplate<String, RedisMessage> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        var template = new RedisTemplate<String, RedisMessage>();
        template.setConnectionFactory(jedisConnectionFactory);
        var jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setKeySerializer(jsonRedisSerializer);
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashKeySerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        return template;
    }

    @Bean
    protected JedisConnectionFactory jedisConnectionFactory() {
        var config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());

        var jedisClientConfiguration = JedisClientConfiguration.builder()
                .readTimeout(Duration.ofSeconds(redisProperties.getReadTimeOut()))
                .connectTimeout(Duration.ofSeconds(redisProperties.getConnectionTimeout()))
                .usePooling()
                .build();

        var factory = new JedisConnectionFactory(config, jedisClientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }
}
