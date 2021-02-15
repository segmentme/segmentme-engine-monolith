package io.segmentme.core.service.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class RedisStreamBuilder {

    private final RedisStreamProperties redisStreamProperties;

    private final StreamReceiver<String, MapRecord<String, Object, Object>> streamReceiver;

    @SneakyThrows
    public Flux<MapRecord<String, Object, Object>> buildAnalysisStream() {
        return streamReceiver.receive(
                Consumer.from(redisStreamProperties.getGroupName(), redisStreamProperties.getConsumerName()),
                StreamOffset.create(redisStreamProperties.getEventKey(), ReadOffset.lastConsumed()));
    }
}
