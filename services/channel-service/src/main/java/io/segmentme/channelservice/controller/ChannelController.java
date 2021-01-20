package io.segmentme.channelservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.channelservice.dto.SdkAnalysisResponse;
import io.segmentme.channelservice.dto.channel.*;
import io.segmentme.redis.dto.SegmentStateChangedMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
public class ChannelController {

    private final static String SDK_ANALYSIS_PATH = "/sdk/synch/analysis/analyze";

    private final static String HEADER_INTEGRATION_POINT_KEY = "integration-point-key";

    private final WebClient webClient;

    private final ReactiveRedisMessageListenerContainer reactiveMsgListenerContainer;

    private final ChannelTopic topic;

    private final ObjectMapper objectMapper;

    @MessageMapping("/subscribe/{integrationPointKey}")
    Flux<MessageOut<?>> channel(@DestinationVariable("integrationPointKey") String integrationPointKey,
                             @Valid Flux<MessageIn> request) {
        log.info("Received subscription request integrationPointKey {}  {}", integrationPointKey, request);

        return request
                .doOnNext(message -> log.info("Received message from client {} ", message))
                .doOnCancel(() -> log.warn("The client cancelled the channel."))
                .switchMap(message -> Flux.concat(analyse(message, integrationPointKey), handleRedisMessage(integrationPointKey)));
    }

    @SneakyThrows
    private Flux<MessageOut<?>> handleRedisMessage(String integrationPointKey) {
        return reactiveMsgListenerContainer
                .receive(topic)
                .doOnNext(message -> log.info("Received message from redis {} ", message))
                .map(ReactiveSubscription.Message::getMessage)
                .map(it -> readValue(it, SegmentStateChangedMessage.class))
                .filter(it -> integrationPointKey.equals(it.getIntegrationPointKey()))
                .map(SegmentStateChangedMessageOut::new);
    }

    private Mono<MessageOut<?>> analyse(MessageIn request, String integrationPointKey) {
        return webClient.post()
                .uri(SDK_ANALYSIS_PATH)
                .header(HEADER_INTEGRATION_POINT_KEY, integrationPointKey)
                .bodyValue(request)
                .exchange()
                .flatMap(it -> it.bodyToMono(SdkAnalysisResponse.class))
                .map(SdkAnalysisResponseMessageOut::new);
    }

    private <T> T readValue(String json, Class<T> target) {
        try {
            return objectMapper.readValue(json, target);
        } catch (JsonProcessingException ex) {
            log.error("Error json parsing", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }
}
