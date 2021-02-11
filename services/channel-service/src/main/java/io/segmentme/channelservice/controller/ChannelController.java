package io.segmentme.channelservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.redis.config.MessagePublisher;
import io.segmentme.redis.config.RedisTopicsBuilder;
import io.segmentme.redis.dto.AnalysisRequest;
import io.segmentme.redis.dto.in.SdkAnalysisMessageIn;
import io.segmentme.redis.dto.out.RedisMessageOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;

import static java.util.UUID.randomUUID;

import static io.segmentme.redis.config.RedisTopicsBuilder.buildSegmentChangedTopic;
import static io.segmentme.redis.config.RedisTopicsBuilder.buildAnalysisResponseTopic;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
public class ChannelController {

    private final ReactiveRedisMessageListenerContainer reactiveMsgListenerContainer;

    private final ObjectMapper objectMapper;

    private final MessagePublisher messageInPublisher;

    private final ThreadPoolTaskExecutor channelExecutor;

    @MessageMapping("/subscribe/{integrationPointKey}/{contextKey}")
    Flux<RedisMessageOut> channel(@DestinationVariable("integrationPointKey") String integrationPointKey, @DestinationVariable("contextKey") String contextKey,
                                  @Valid Flux<AnalysisRequest> request) {

        log.info("Received subscription request integrationPointKey {}  {}", integrationPointKey, request);
        final String requesterId = randomUUID().toString();

        return request
                .parallel(4)
                .runOn(Schedulers.fromExecutor(channelExecutor))
                .doOnNext(message -> log.info("Received message from client {} ", message))
                .doOnCancel(() -> log.warn("The client cancelled the channel."))
                .map(it -> prepareRequest(integrationPointKey, requesterId, contextKey, it))
                .doOnNext(req -> messageInPublisher.publish(req, RedisTopicsBuilder.ANALYSIS_REQUEST_TOPIC.getTopic()))
                .sequential()
                .switchMap(message -> handleSegmentChangeMessage(integrationPointKey, contextKey, requesterId));
    }

    private Flux<RedisMessageOut> handleSegmentChangeMessage(String integrationPointKey, String contextKey, String requesterId) {
        return reactiveMsgListenerContainer
                .receive(buildSegmentChangedTopic(integrationPointKey), buildAnalysisResponseTopic(integrationPointKey, contextKey, requesterId))
                .doOnNext(message -> log.info("Received message from redis {} ", message))
                .map(ReactiveSubscription.Message::getMessage)
                .map(it -> readValue(it, RedisMessageOut.class));
    }

    private SdkAnalysisMessageIn prepareRequest(String integrationPointKey, String requesterId,
                                                String contextKey, AnalysisRequest body) {
        SdkAnalysisMessageIn request = new SdkAnalysisMessageIn();
        request.setIntegrationPointKey(integrationPointKey);
        request.setRequesterId(requesterId);
        request.setBody(body.setContextKey(contextKey));
        return request;
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
