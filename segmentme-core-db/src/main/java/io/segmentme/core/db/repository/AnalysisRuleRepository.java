package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.rule.SimpleAnalysisRule;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalysisRuleRepository extends MongoRepository<SimpleAnalysisRule<?>, String> {
}
