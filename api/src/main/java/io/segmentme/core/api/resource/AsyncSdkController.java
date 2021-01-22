package io.segmentme.core.api.resource;

import io.segmentme.core.api.facade.SdkFacade;
import io.segmentme.core.service.dto.analysis.SdkAnalysisRequest;
import io.segmentme.core.service.dto.analysis.SdkAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AsyncSdkController {

    private final SdkFacade sdkFacade;

    @MessageMapping("sdk.asynch.analyze.{integrationPointKey}")
    public Flux<SdkAnalysisResponse> analyze(@DestinationVariable("integrationPointKey") String integrationPointKey,
                                             Flux<SdkAnalysisRequest> sdkAnalysisRequest) {
        return sdkAnalysisRequest
            .parallel(4).runOn(Schedulers.newParallel("analysis", 4))
            .doOnNext(message -> log.info("Received message from client {} ", message))
            .doOnCancel(() -> log.warn("The client cancelled the channel."))
            .map(message -> sdkFacade.analyze(integrationPointKey, message))
            .sequential();
    }
}
