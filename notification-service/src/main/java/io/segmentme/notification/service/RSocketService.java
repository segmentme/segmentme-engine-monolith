package io.segmentme.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RSocketService {

    private final static Map<String, Map<String, RSocketRequester>> socketClient = new ConcurrentHashMap<>();

    public void addClient(String clientId, String integrationPointKey, RSocketRequester requester){
        socketClient
                .computeIfAbsent(clientId, key -> new ConcurrentHashMap<>())
                .computeIfAbsent(integrationPointKey, key -> requester);
    }

    public void deleteAll(){
        socketClient.clear();
    }

    public void delete(String clientId, String integrationPointKey){
        Optional.ofNullable(socketClient.get(clientId))
                .ifPresent(it ->{
                    it.remove(integrationPointKey);
                    if (CollectionUtils.isEmpty(it.values())) {
                        socketClient.remove(clientId);
                    }
                });
    }

    public Optional<RSocketRequester> findClient(){
      return socketClient.values()
                .stream()
                .findFirst()
                .map(it -> it.values().stream().findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
}
