package io.segmentme.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories(basePackages = "io.segmentme")
public abstract class RedisConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    protected LettuceConnectionFactory redisConnectionFactory() {
        var config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        var factory = new LettuceConnectionFactory(config, LettuceClientConfiguration.builder().build());
        return factory;

    }
}
