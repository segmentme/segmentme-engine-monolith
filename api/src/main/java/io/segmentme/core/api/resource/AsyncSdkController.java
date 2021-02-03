package io.segmentme.core.api.resource;

import io.segmentme.core.api.facade.SdkFacade;
import io.segmentme.core.service.dto.analysis.SdkAnalysisRequest;
import io.segmentme.core.service.dto.analysis.SdkAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AsyncSdkController {

    private final SdkFacade sdkFacade;

    private final ThreadPoolTaskExecutor channelExecutor;

    @MessageMapping("sdk.asynch.analyze.{integrationPointKey}")
    public Mono<SdkAnalysisResponse> analyze(@DestinationVariable("integrationPointKey") String integrationPointKey,
                                             Mono<SdkAnalysisRequest> sdkAnalysisRequest) {
        return sdkAnalysisRequest
                .map(it -> sdkFacade.analyze(integrationPointKey, it));
    }
}
