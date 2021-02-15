package io.segmentme.core.api.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.api.facade.SdkFacade;
import io.segmentme.core.api.redis.message.SdkAnalysisMessage;
import io.segmentme.core.service.config.RedisStreamBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisRedisService {

    private final ObjectMapper objectMapper;

    private final SdkFacade sdkFacade;

    private final ThreadPoolTaskExecutor channelExecutor;

    private final RedisStreamBuilder redisStreamBuilder;

    @PostConstruct
    private void init() {
        this.handleRedisMessage();
    }

    private void handleRedisMessage() {
        redisStreamBuilder.buildAnalysisStream()
                .doOnNext(message -> log.info("Received message from redis {} ", message))
                .doOnError(err -> log.error("Redis stream error", err))
                .onErrorResume(t -> Flux.empty())
                .doOnCancel(() -> log.info("Redis stream was cancelled"))
                .doOnTerminate(() -> log.info("Redis stream terminated"))
                .parallel(10)
                .runOn(Schedulers.fromExecutor(channelExecutor))
                .map(Record::getValue)
                .map(it -> objectMapper.convertValue(it, SdkAnalysisMessage.class))
                .subscribe(sdkFacade::analyseMessage, err -> log.error("Analysis error", err));
    }
}
