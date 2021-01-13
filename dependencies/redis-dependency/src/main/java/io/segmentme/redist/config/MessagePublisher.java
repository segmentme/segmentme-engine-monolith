package io.segmentme.redist.config;

import io.segmentme.redist.dto.RedisMessage;

public interface MessagePublisher {

    void publish(final RedisMessage<?> message);
}
