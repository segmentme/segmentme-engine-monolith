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

import static io.segmentme.redis.config.RedisTopicsBuilder.buildAnalysisResponseTopic;
import static io.segmentme.redis.config.RedisTopicsBuilder.buildSegmentChangedTopic;
import static java.util.UUID.randomUUID;

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
        final String requesterId = randomUUID().toString();
        log.info("Received subscription request integrationPointKey={} contextKey={} requesterId={}", integrationPointKey, contextKey, requesterId);

        return request
                .doOnNext(message -> log.debug("Received message from client {} ", message))
                .doOnSubscribe(it -> log.info("Subscribed client integrationPointKey={} contextKey={} requesterId={}", integrationPointKey, contextKey, requesterId))
                .doOnError(er -> log.error("Client subscription integrationPointKey={} contextKey={} requesterId={} error", integrationPointKey, contextKey, requesterId, er))
                .doOnCancel(() -> log.warn("The client integrationPointKey={} contextKey={} requesterId={} cancelled the channel.", integrationPointKey, contextKey, requesterId))
                .parallel()
                .runOn(Schedulers.fromExecutor(channelExecutor))
                .map(it -> prepareRequest(integrationPointKey, requesterId, contextKey, it))
                .sequential()
                .switchMap(message -> handleMessages(integrationPointKey, contextKey, requesterId)
                        .doOnSubscribe(it -> messageInPublisher.publish(message, RedisTopicsBuilder.ANALYSIS_REQUEST_TOPIC.getTopic())));
    }

    private Flux<RedisMessageOut> handleMessages(String integrationPointKey, String contextKey, String requesterId) {
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
