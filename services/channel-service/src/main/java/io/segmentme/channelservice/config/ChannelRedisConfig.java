package io.segmentme.channelservice.config;

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
public class ChannelRedisConfig extends RedisConfig {

    @Bean
    protected RedisMessageListenerContainer redisContainer(RedisMessageSubscriber messageSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(messageListener(messageSubscriber), clientAnalysisStateChanged());
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListener(RedisMessageSubscriber messageSubscriber) {
        return new MessageListenerAdapter(messageSubscriber);
    }

    @Bean
    public MessagePublisher segmentChangePublisher(JedisConnectionFactory jedisConnectionFactory) {
        return new RedisMessagePublisher(clientAnalysisStateChanged(), redisTemplate(jedisConnectionFactory));
    }

    @Bean
    public ChannelTopic clientAnalysisStateChanged() {
        return new ChannelTopic("clientAnalysisStateChangedQueue");
    }
}
