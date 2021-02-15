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

    private final ReactiveRedisMessageListenerContainer reactiveMsgListenerContainer;

    private final ObjectMapper objectMapper;

    private final ThreadPoolTaskExecutor channelExecutor;

    private final RedisTemplate<String, Object> redisTemplate;


    @Value("${segmentme.application.redis.stream.analysisStreamKey}")
    private final String analysisStreamKey;

    @MessageMapping("/subscribe/{integrationPointKey}/{contextKey}/{clientId}")
    Flux<RedisMessageOut> channel(@DestinationVariable("integrationPointKey") String integrationPointKey,
                                  @DestinationVariable("contextKey") String contextKey,
                                  @DestinationVariable("clientId") String clientId,
                                  @Valid Flux<AnalysisRequest> request) {
        log.info("Received subscription request integrationPointKey={} contextKey={} clientId={}", integrationPointKey, contextKey, clientId);

        return request
                .doOnNext(message -> log.debug("Received message from client {} ", message))
                .doOnSubscribe(it -> log.info("Subscribed client integrationPointKey={} contextKey={} clientId={}", integrationPointKey, contextKey, clientId))
                .doOnError(er -> log.error("Client subscription integrationPointKey={} contextKey={} clientId={} error", integrationPointKey, contextKey, clientId, er))
                .doOnCancel(() -> log.warn("The client integrationPointKey={} contextKey={} clientId={} cancelled the channel.", integrationPointKey, contextKey, clientId))
                .map(it -> prepareRequest(integrationPointKey, contextKey, it))
                .switchMap(message -> handleMessages(integrationPointKey, contextKey, clientId)
                        .doOnSubscribe(it -> publishAnalysisMessageStream(message)));
    }

    private Flux<RedisMessageOut> handleMessages(String integrationPointKey, String contextKey, String clientId) {
        return reactiveMsgListenerContainer
                .receive(buildSegmentChangedTopic(integrationPointKey), buildAnalysisResponseTopic(integrationPointKey, contextKey, clientId))
                .parallel(5)
                .runOn(Schedulers.fromExecutor(channelExecutor))
                .doOnNext(message -> log.info("Received message from redis {} ", message))
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
