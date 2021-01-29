package io.segmentme.channelservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.routing.client.spring.RoutingRSocketRequester;
import io.segmentme.channelservice.dto.SdkAnalysisResponse;
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

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.time.Duration;

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

    private final RoutingRSocketRequester requester;

    private final ThreadPoolTaskExecutor channelExecutor;

    @PostConstruct
    private void init() {
        requester
            .route("sdk.asynch.analyze.{integrationPointKey}", "9356674eaa974cb2b8871d8afb1013bd")
            .data(Flux.generate(it -> it.next(new SdkAnalysisMessageIn().setContextKey("channel-user-payload").setAnalysisData(new SdkAnalysisMessageIn.AnalysisData().setClientId("11"))))
                .delayElements(Duration.ofSeconds(5))
                .doOnNext(message -> log.info("Send to analysis")))
            .retrieveFlux(SdkAnalysisResponse.class)
            .map(SdkAnalysisResponseMessageOut::new).subscribe(it -> log.info("Result {}", it));
    }


    @MessageMapping("/subscribe/{integrationPointKey}/{clientId}")
    Flux<MessageOut<?>> channel(@DestinationVariable("integrationPointKey") String integrationPointKey, @DestinationVariable("clientId") String clientId,
                                @Valid Flux<MessageIn> request) {
        log.info("Received subscription request integrationPointKey {}  {}", integrationPointKey, request);

        return request
            .parallel(4)
            .runOn(Schedulers.fromExecutor(channelExecutor))
            .doOnNext(message -> log.info("Received message from client {} ", message))
            .doOnCancel(() -> log.warn("The client cancelled the channel."))
            .map(message -> Flux.concat(analyse(message, integrationPointKey), handleRedisMessage(integrationPointKey)))
            .sequential()
            .switchMap(Flux::merge);
    }

    private Flux<MessageOut<?>> handleRedisMessage(String integrationPointKey) {
        return reactiveMsgListenerContainer
            .receive(topic)
            .doOnNext(message -> log.info("Received message from redis {} ", message))
            .map(ReactiveSubscription.Message::getMessage)
            .map(it -> readValue(it, SegmentStateChangedMessage.class))
            .filter(it -> integrationPointKey.equals(it.getIntegrationPointKey()))
            .map(SegmentStateChangedMessageOut::new);
    }

    private Flux<MessageOut<?>> analyse(MessageIn request, String integrationPointKey) {
        return requester.route("sdk.asynch.analyze.{integrationPointKey}", integrationPointKey)
            .data(Flux.just(request).doOnNext(message -> log.info("Send to analysis")))
            .retrieveFlux(SdkAnalysisResponse.class)
            .map(SdkAnalysisResponseMessageOut::new);


//        return webClient.post()
//                .uri(SDK_ANALYSIS_PATH)
//                .header(HEADER_INTEGRATION_POINT_KEY, integrationPointKey)
//                .bodyValue(request)
//                .exchange()
//                .flatMap(it -> it.bodyToMono(SdkAnalysisResponse.class))
//                .map(SdkAnalysisResponseMessageOut::new);
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
