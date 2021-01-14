package io.segmentme.redis.repository;

import io.segmentme.redis.domain.ConnectedClient;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectedClientRepository extends KeyValueRepository<ConnectedClient, String> {

    Optional<ConnectedClient> findByIntegrationPointKeyAndClientIdAndSessionId(String integrationPointKey, String clientId, String sessionId);

    void deleteByIntegrationPointKeyAndClientIdAndSessionId(String integrationPointKey, String clientId, String sessionId);

    List<ConnectedClient> findByIntegrationPointKey(String integrationPointKey);
}
