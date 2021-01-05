package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.context.ContextSchema;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ContextSchemaRepository extends MongoRepository<ContextSchema, String> {
    List<ContextSchema> findByIntegrationPointKeyIn(Collection<String> integrationPointKeys);

    @Query(fields = "{ 'id' : 1,'name':1, 'integrationPointKey':1, 'hash':1 }")
    List<ContextSchema> findShortFormByIntegrationPointKeyIn(Collection<String> integrationPointKeys);

    Optional<ContextSchema> findByIntegrationPointKey(String integrationPointKey);

    @Query(fields = "{ 'id' : 1,'name':1, 'integrationPointKey':1,'inlinePath':1 }")
    Optional<ContextSchema> findByIntegrationPointKeyAndHash(String integrationPointKey, String hash);

    Optional<ContextSchema> findByIdAndIntegrationPointKey(String id, String integrationPointKey);
}
