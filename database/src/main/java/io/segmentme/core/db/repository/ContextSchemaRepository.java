package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.context.ContextSchema;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ContextSchemaRepository extends MongoRepository<ContextSchema, String> {
    Optional<ContextSchema> findByIntegrationPointKey(String integrationPointKey);

    List<ContextSchema> findByIntegrationPointKeyIn(Collection<String> integrationPointKeys);
}
