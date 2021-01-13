package io.segmentme.channelservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.channelservice.rsocket.RSocketConnectionHandler;
import io.segmentme.redist.config.MessagePublisher;
import io.segmentme.redist.dto.RedisMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChannelController {

    private final MessagePublisher messagePublisher;

    private final RSocketConnectionHandler rSocketConnectionHandler;

    @MessageMapping("/subscribe/{clientId}/{integrationPointKey}")
    Mono<?> channel(RSocketRequester requester,
                    @DestinationVariable("clientId") String clientId,
                    @DestinationVariable("integrationPointKey") String integrationPointKey,
                    @Payload JsonNode payload) {
        log.info("Received subscription request client {} integrationPointKey {}  {}", clientId, integrationPointKey, payload);
        rSocketConnectionHandler.handleSession(requester, clientId, integrationPointKey);
        return Mono.never();
    }

    @Scheduled(fixedRate = 6000)
    public void reportCurrentTime() {
        IntStream.range(0, 10)
                .mapToObj(it -> new RedisMessage<Map<?, ?>>().setIntegrationPointKey("ca79e73c-1dca-4b00-8b2b-1f14dbee9012")
                        .setClientId("ca79e73c-1dca-4b00-8b2b-1f14dbee900c")
                        .setBody(Map.of("name", randomUUID().toString())))
                .forEach(messagePublisher::publish);
    }
}
