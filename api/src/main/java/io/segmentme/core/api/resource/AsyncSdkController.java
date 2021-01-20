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

@Slf4j
@Controller
@RequiredArgsConstructor
public class AsyncSdkController {

    private final SdkFacade sdkFacade;

    @MessageMapping("sdk.asynch.analyze.{integrationPointKey}")
    public Flux<SdkAnalysisResponse> analyze(@DestinationVariable("integrationPointKey") String integrationPointKey,
                                             Flux<SdkAnalysisRequest> sdkAnalysisRequest) {
        return sdkAnalysisRequest
                .doOnNext(message -> log.info("Received message from client {} ", message))
                .doOnCancel(() -> log.warn("The client cancelled the channel."))
                .map(it -> sdkFacade.analyze(integrationPointKey, it));
    }
}
