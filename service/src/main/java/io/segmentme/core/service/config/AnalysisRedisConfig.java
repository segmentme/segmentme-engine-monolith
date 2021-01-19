package io.segmentme.core.service.config;

import io.segmentme.redis.config.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class AnalysisRedisConfig extends RedisConfig {

    @Bean
    protected RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        return container;
    }

    @Bean
    public MessagePublisher analysisTopicPublisher(JedisConnectionFactory jedisConnectionFactory) {
        return new RedisMessagePublisher(analysisTopic(), redisTemplate(jedisConnectionFactory));
    }

    @Bean
    public ChannelTopic analysisTopic() {
        return new ChannelTopic("analysisChannelTopic");
    }
}
