package io.segmentme.core.api.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.api.facade.SdkFacade;
import io.segmentme.core.api.redis.message.SdkAnalysisMessage;
import io.segmentme.redis.config.RedisTopicsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisRedisService {

    private final ObjectMapper objectMapper;

    private final ReactiveRedisMessageListenerContainer reactiveMsgListenerContainer;

    private final SdkFacade sdkFacade;

    private final ThreadPoolTaskExecutor channelExecutor;

    @PostConstruct
    private void init() {
        this.handleRedisMessage();
    }

    private void handleRedisMessage() {
        reactiveMsgListenerContainer
                .receive(RedisTopicsBuilder.ANALYSIS_REQUEST_TOPIC)
                .doOnNext(message -> log.info("Received message from redis {} ", message))
                .parallel(10)
                .runOn(Schedulers.fromExecutor(channelExecutor))
                .map(ReactiveSubscription.Message::getMessage)
                .map(it -> readValue(it, SdkAnalysisMessage.class))
                .doOnNext(sdkFacade::analyseMessage)
                .subscribe();
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
