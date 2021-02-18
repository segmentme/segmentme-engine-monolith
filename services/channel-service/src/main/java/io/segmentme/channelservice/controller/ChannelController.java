package io.segmentme.channelservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.redis.dto.AnalysisRequest;
import io.segmentme.redis.dto.in.SdkAnalysisMessageIn;
import io.segmentme.redis.dto.out.RedisMessageOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.Map;

import static io.segmentme.redis.config.RedisTopicsBuilder.buildAnalysisResponseTopic;
import static io.segmentme.redis.config.RedisTopicsBuilder.buildSegmentChangedTopic;

@Slf4j
@EnableScheduling
@Validated
@Controller
@RequiredArgsConstructor
public class ChannelController {

    private final ObjectMapper objectMapper;

    private final ThreadPoolTaskExecutor channelExecutor;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ReactiveRedisConnectionFactory factory;

    @Value("${segmentme.application.redis.stream.analysisStreamKey}")
    private final String analysisStreamKey;

    @MessageMapping("/subscribe/{integrationPointKey}/{contextKey}/{clientId}")
    Flux<RedisMessageOut> channel(@DestinationVariable("integrationPointKey") String integrationPointKey,
                                  @DestinationVariable("contextKey") String contextKey,
                                  @DestinationVariable("clientId") String clientId,
                                  @Valid Flux<AnalysisRequest> request) {

        Hooks.onErrorDropped(ignoreError -> {
        });

        return redisMessagesHandler(integrationPointKey, contextKey, clientId)
                .doOnSubscribe(it -> this.producerMessageHandler(request, integrationPointKey, contextKey, clientId))
                .doOnSubscribe(it -> log.info("Subscribed client integrationPointKey={} contextKey={} clientId={}", integrationPointKey, contextKey, clientId))
                .doOnError(er -> log.error("Client subscription integrationPointKey={} contextKey={} clientId={} error", integrationPointKey, contextKey, clientId, er))
                .doOnCancel(() -> log.warn("Client integrationPointKey={} contextKey={} clientId={} cancelled the channel.", integrationPointKey, contextKey, clientId));
    }

    private void producerMessageHandler(Flux<AnalysisRequest> producerRequest, String integrationPointKey, String contextKey, String clientId) {
        producerRequest
                .map(it -> prepareRequest(integrationPointKey, contextKey, it))
                .doOnNext(this::publishAnalysisMessageStream).subscribe();
    }

    private Flux<RedisMessageOut> redisMessagesHandler(String integrationPointKey, String contextKey, String clientId) {
        return new ReactiveRedisMessageListenerContainer(factory)
                .receive(buildSegmentChangedTopic(integrationPointKey), buildAnalysisResponseTopic(integrationPointKey, contextKey, clientId))
                .doOnNext(message -> log.info("Received message from redis {} ", message))
                .doFinally(ignore -> log.info("Redis subscription terminated for integrationPointKey={} contextKey={} clientId={} cancelled the channel.", integrationPointKey, contextKey, clientId))
                .parallel(2)
                .runOn(Schedulers.fromExecutor(channelExecutor))
                .map(ReactiveSubscription.Message::getMessage)
                .map(it -> readValue(it, RedisMessageOut.class))
                .sequential();
    }

    private SdkAnalysisMessageIn prepareRequest(String integrationPointKey, String contextKey, AnalysisRequest body) {
        SdkAnalysisMessageIn request = new SdkAnalysisMessageIn();
        request.setIntegrationPointKey(integrationPointKey);
        request.setBody(body.setContextKey(contextKey));
        return request;
    }

    private void publishAnalysisMessageStream(SdkAnalysisMessageIn message) {
        Map<Object, Object> request = objectMapper.convertValue(message, new TypeReference<>() {});

        var streamMessage = StreamRecords.newRecord()
                .ofMap(request)
                .withStreamKey(analysisStreamKey);

        redisTemplate.opsForStream().add(streamMessage);
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
