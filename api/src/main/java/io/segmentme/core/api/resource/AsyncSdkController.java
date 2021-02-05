package io.segmentme.core.api.resource;

import io.segmentme.core.api.facade.SdkFacade;
import io.segmentme.core.service.dto.analysis.SdkAnalysisRequest;
import io.segmentme.core.service.dto.analysis.SdkAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AsyncSdkController {

    private final SdkFacade sdkFacade;

    private final Scheduler analyseScheduler;

    @MessageMapping("sdk.asynch.analyze.{integrationPointKey}")
    public Mono<SdkAnalysisResponse> analyze(@DestinationVariable("integrationPointKey") String integrationPointKey,
                                             Mono<SdkAnalysisRequest> sdkAnalysisRequest) {
        return sdkAnalysisRequest
        .subscribeOn(analyseScheduler)
//            .doOnError(it -> {
//                log.error("Error on subscription", it);
//            })
            .doOnNext(it -> log.info("Start analysis"))
            .map(it -> sdkFacade.analyze(integrationPointKey, it))
//            .subscribeOn(Schedulers.fromExecutor(channelExecutor))
            .doOnNext(it -> log.info("Done analysis"));
    }
}
