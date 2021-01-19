package io.segmentme.channelservice.rsocket;

import io.segmentme.channelservice.dto.RSocketSessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
class RSocketSessionManager {

    private final static Map<String, Map<String, List<RSocketSessionHolder>>> socketClient = new ConcurrentHashMap<>();

    public void addClient(String clientId, String integrationPointKey, String sessionId, RSocketRequester requester) {
        socketClient
                .computeIfAbsent(clientId, key -> new ConcurrentHashMap<>())
                .computeIfAbsent(integrationPointKey, key -> Collections.synchronizedList(new ArrayList<>()))
                .add(RSocketSessionHolder.of(sessionId, requester));
    }

    public void delete(String clientId, String integrationPointKey, String sessionId) {
        ofNullable(socketClient.get(clientId))
                .ifPresent(it -> {
                    List<RSocketSessionHolder> sessionHolders = it.get(integrationPointKey);
                    sessionHolders.removeIf(sessionHolder -> sessionId.equals(sessionHolder.getId()));

                    if (CollectionUtils.isEmpty(sessionHolders)) {
                        it.remove(integrationPointKey);
                        socketClient.remove(clientId);
                    }
                });
    }

    public List<RSocketSessionHolder> findClients(String integrationPointKey) {
        return socketClient.values()
                .stream()
                .map(stringListMap -> Optional.ofNullable(stringListMap.get(integrationPointKey)).orElse(null))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        socketClient.clear();
    }
}
