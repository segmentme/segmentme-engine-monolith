package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.context.AnalysisContext;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalysisContextRepository extends MongoRepository<AnalysisContext, String> {

}
