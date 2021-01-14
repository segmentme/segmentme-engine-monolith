package io.segmentme.core.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.redis.dto.RedisMessage;
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
public class AnalysisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    private final ApplicationEventPublisher eventPublisher;

    @SneakyThrows
    public void onMessage(Message message, byte[] pattern) {
        log.info("Message received: {}", message.toString());
        RedisMessage redisMessage = objectMapper.readValue(message.getBody(), RedisMessage.class);
        eventPublisher.publishEvent(redisMessage);
    }
}
