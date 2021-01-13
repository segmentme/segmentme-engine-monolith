
package io.segmentme.redist.config;

import io.segmentme.redist.dto.RedisMessage;
import lombok.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {

    private final ChannelTopic topic;

    private final RedisTemplate<String, RedisMessage<?>> redisTemplate;

    @Override
    public void publish(RedisMessage<?> message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
