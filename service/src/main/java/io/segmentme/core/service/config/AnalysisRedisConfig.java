package io.segmentme.core.service.config;

import io.segmentme.redis.config.MessagePublisher;
import io.segmentme.redis.config.RedisConfig;
import io.segmentme.redis.config.RedisMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class AnalysisRedisConfig extends RedisConfig {

    @Bean
    protected RedisMessageListenerContainer redisContainer(AnalysisMessageSubscriber messageSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(messageListener(messageSubscriber), analysisTopic());
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListener(AnalysisMessageSubscriber messageSubscriber) {
        return new MessageListenerAdapter(messageSubscriber);
    }

    @Bean
    public MessagePublisher analysisTopicPublisher(JedisConnectionFactory jedisConnectionFactory) {
        return new RedisMessagePublisher(analysisTopic(), redisTemplate(jedisConnectionFactory));
    }

    @Bean
    public MessagePublisher clientAnalysisStateChangedPublisher(JedisConnectionFactory jedisConnectionFactory) {
        return new RedisMessagePublisher(clientAnalysisStateChanged(), redisTemplate(jedisConnectionFactory));
    }

    @Bean
    public ChannelTopic clientAnalysisStateChanged() {
        return new ChannelTopic("clientAnalysisStateChangedQueue");
    }

    @Bean
    public ChannelTopic analysisTopic() {
        return new ChannelTopic("analysisChannelTopic");
    }
}
