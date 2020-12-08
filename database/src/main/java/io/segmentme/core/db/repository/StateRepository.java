package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.state.State;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StateRepository extends MongoRepository<State, String> {

    List<State> findByIntegrationPointKey(String integrationPoint);

    List<State> findByIntegrationPointKeyIn(Iterable<String> integrationPoint);

    Optional<State> findByNameAndIntegrationPointKey(String name, String integrationPointKey);
}
