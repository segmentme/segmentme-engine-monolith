
package io.segmentme.redis.config;

import io.segmentme.redis.dto.RedisMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {

    private final ChannelTopic topic;

    private final RedisTemplate<String, RedisMessage> redisTemplate;

    @Override
    public void publish(RedisMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
