package io.segmentme.channelservice.rsocket;

import io.segmentme.channelservice.dto.RSocketSessionHolder;
import io.segmentme.redis.dto.ReanalysisMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Collection;

import static java.util.UUID.randomUUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RSocketConnectionHandler {

    private final static String CALL_BACK_ROUTE = "segmentme-call-back";

    private final RSocketSessionManager socketSessionManager;

    @EventListener(ReanalysisMessage.class)
    @Async
    public void handleMessage(ReanalysisMessage redisMessage) {
        socketSessionManager.findClients(redisMessage.getIntegrationPointKey())
                .stream()
                .parallel()
                .map(RSocketSessionHolder::getRequester)
                .forEach(it -> sendMessage(it, redisMessage));
    }

    public void handleSession(RSocketRequester requester, String clientId, String integrationPointKey) {
        var sessionId = randomUUID().toString();
        requester.rsocket()
                .onClose()
                .doFirst(() -> {
                    log.info("Connected client sessionId={} clientId={} integrationPointKey={}", sessionId, clientId, integrationPointKey);
                    this.socketSessionManager.addClient(clientId, integrationPointKey, sessionId, requester);
                })
                .doOnError(error -> log.warn("Client error sessionId={} clientId={} integrationPointKey={}", sessionId, clientId, integrationPointKey))
                .doFinally(consumer -> {
                    this.socketSessionManager.delete(clientId, integrationPointKey, sessionId);
                    log.info("Client disconnected sessionId={} clientId={} integrationPointKey={}", sessionId, clientId, integrationPointKey);
                })
                .subscribe();
    }

    private void sendMessage(RSocketRequester requester, ReanalysisMessage body) {
        requester.route(CALL_BACK_ROUTE)
                .data(body)
                .retrieveMono(String.class)
                .subscribe();
    }

    @PreDestroy
    private void shutdown() {
        log.info("Detaching all remaining clients...");
        socketSessionManager.deleteAll();
        log.info("Shutting down.");
    }
}
