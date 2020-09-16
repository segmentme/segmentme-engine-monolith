package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.context.ContextSchema;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalysisContextSchemaRepository extends MongoRepository<ContextSchema, String> {

}
