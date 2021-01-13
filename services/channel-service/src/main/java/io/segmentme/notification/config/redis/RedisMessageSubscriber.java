package io.segmentme.notification.config.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.redist.dto.RedisMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    private final ApplicationEventPublisher eventPublisher;

    @SneakyThrows
    public void onMessage(Message message, byte[] pattern) {
        log.info("Message received: {}", message.toString());
        RedisMessage<JsonNode> jsonNodeRedisMessage = objectMapper.readValue(message.getBody(), new TypeReference<>() {});
        eventPublisher.publishEvent(jsonNodeRedisMessage);
    }
}
