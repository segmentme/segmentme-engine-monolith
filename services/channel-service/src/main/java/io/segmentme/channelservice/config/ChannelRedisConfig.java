package io.segmentme.channelservice.config;

import io.segmentme.redis.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        container.addMessageListener(messageListener(messageSubscriber), analysisChannelTopic());
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListener(RedisMessageSubscriber messageSubscriber) {
        return new MessageListenerAdapter(messageSubscriber);
    }

    @Bean
    public ChannelTopic analysisChannelTopic() {
        return new ChannelTopic("analysisChannelTopic");
    }
}
