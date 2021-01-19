package io.segmentme.channelservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.channelservice.dto.SdkAnalysisRequest;
import io.segmentme.channelservice.rsocket.RSocketConnectionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
public class ChannelController {

    private final static String SDK_ANALYSIS_PATH = "/sdk/synch/analysis/analyze";

    private final static String HEADER_INTEGRATION_POINT_KEY = "integration-point-key";

    private final WebClient webClient;

    private final RSocketConnectionHandler rSocketConnectionHandler;


    @MessageMapping("/subscribe/{sessionId}/{integrationPointKey}")
    Mono<?> channel(RSocketRequester requester,
                    @DestinationVariable("sessionId") String sessionId,
                    @DestinationVariable("integrationPointKey") String integrationPointKey,
                    @Payload @Valid SdkAnalysisRequest request) {
        log.info("Received subscription request integrationPointKey {}  {}", integrationPointKey, request);
        rSocketConnectionHandler.handleSession(requester, request.getAnalysisData().getClientId(), integrationPointKey, sessionId);

        return webClient.post()
                .uri(SDK_ANALYSIS_PATH)
                .header(HEADER_INTEGRATION_POINT_KEY, integrationPointKey)
                .bodyValue(request)
                .exchange()
                .flatMap(it -> it.bodyToMono(JsonNode.class));
    }
}
