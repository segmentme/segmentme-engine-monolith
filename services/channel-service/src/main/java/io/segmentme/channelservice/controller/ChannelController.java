package io.segmentme.channelservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.channelservice.dto.channel.*;
import io.segmentme.redis.dto.SegmentStateChangedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

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

    private final ThreadPoolTaskExecutor channelExecutor;


    @MessageMapping("subscribe.{integrationPointKey}")
    Flux<SegmentStateChangedMessageOut> channel(@DestinationVariable("integrationPointKey") String integrationPointKey) {
        log.info("Received subscription request integrationPointKey {}", integrationPointKey);
        return this.handleRedisMessage(integrationPointKey);
    }

    private Flux<SegmentStateChangedMessageOut> handleRedisMessage(String integrationPointKey) {
        return reactiveMsgListenerContainer
                .receive(topic)
                .parallel(4)
                .runOn(Schedulers.fromExecutor(channelExecutor))
                .doOnNext(message -> log.info("Received message from redis {} ", message))
                .map(ReactiveSubscription.Message::getMessage)
                .map(it -> readValue(it, SegmentStateChangedMessage.class))
                .filter(it -> integrationPointKey.equals(it.getIntegrationPointKey()))
                .map(SegmentStateChangedMessageOut::new)
                .sequential();
    }



//        return webClient.post()
//                .uri(SDK_ANALYSIS_PATH)
//                .header(HEADER_INTEGRATION_POINT_KEY, integrationPointKey)
//                .bodyValue(request)
//                .exchange()
//                .flatMap(it -> it.bodyToMono(SdkAnalysisResponse.class))
//                .map(SdkAnalysisResponseMessageOut::new);

    private <T> T readValue(String json, Class<T> target) {
        try {
            return objectMapper.readValue(json, target);
        } catch (JsonProcessingException ex) {
            log.error("Error json parsing", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }
}
