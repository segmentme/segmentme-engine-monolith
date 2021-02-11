
package io.segmentme.redis.config;

import io.segmentme.redis.dto.RedisMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {

    private final RedisTemplate<String, RedisMessage> redisTemplate;

    @Override
    public void publish(RedisMessage message, String topic) {
        redisTemplate.convertAndSend(topic, message);
    }
}
