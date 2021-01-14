package io.segmentme.redis.config;

import io.segmentme.redis.dto.RedisMessage;

public interface MessagePublisher {

    void publish(final RedisMessage message);
}
