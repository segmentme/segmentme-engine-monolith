package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.context.ContextSchema;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ContextSchemaRepository extends MongoRepository<ContextSchema, String> {
    List<ContextSchema> findByIntegrationPointKeyIn(Collection<String> integrationPointKeys);

    @Query(fields = "{ 'id' : 1,'name':1, 'integrationPointKey':1 }")
    List<ContextSchema> findShortFormByIntegrationPointKeyIn(Collection<String> integrationPointKeys);
}
