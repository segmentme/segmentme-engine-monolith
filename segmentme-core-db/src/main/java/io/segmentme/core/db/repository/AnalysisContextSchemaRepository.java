package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.context.AnalysisContextSchema;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalysisContextSchemaRepository extends MongoRepository<AnalysisContextSchema, String> {

}
