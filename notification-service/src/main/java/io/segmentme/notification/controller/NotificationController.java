package io.segmentme.notification.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.util.internal.StringUtil;
import io.segmentme.notification.service.RSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final RSocketService rSocketService;

    @PreDestroy
    private void shutdown() {
        log.info("Detaching all remaining clients...");
        rSocketService.deleteAll();
        log.info("Shutting down.");
    }

    @MessageMapping("/subscribe/{clientId}/{integrationPointKey}")
    Mono<?> channel(RSocketRequester requester,
                    @DestinationVariable("clientId") String clientId,
                    @DestinationVariable("integrationPointKey") String integrationPointKey,
                    @Payload JsonNode payload) {
        log.info("Received subscription request client {} integrationPointKey {}  {}", clientId, integrationPointKey, payload);
        rSocketRequesterInitialization(requester, clientId, integrationPointKey);
        return Mono.never();
    }


    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        Optional<RSocketRequester> requester = rSocketService.findClient();
        requester.ifPresent(socketRequester ->
                socketRequester.route("client-status")
                        .data(Map.of("origin", UUID.randomUUID().toString(), "interaction", UUID.randomUUID().toString()))
                        .retrieveMono(String.class)
                        .subscribe());
    }

    private void rSocketRequesterInitialization(RSocketRequester requester, String clientId, String integrationPointKey) {
        requester.rsocket()
                .onClose()
                .doFirst(() -> {
                    log.info("Client: {} CONNECTED.", integrationPointKey);
                    this.rSocketService.addClient(clientId, integrationPointKey, requester);
                })
                .doOnError(error -> log.warn("Channel to client {} CLOSED", integrationPointKey))
                .doFinally(consumer -> {
                    this.rSocketService.delete(clientId, integrationPointKey);
                    log.info("Client {} DISCONNECTED", integrationPointKey);
                })
                .subscribe();
    }
}
