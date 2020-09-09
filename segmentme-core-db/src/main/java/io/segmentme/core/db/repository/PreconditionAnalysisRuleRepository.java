package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.rule.PreconditionAnalysisRule;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreconditionAnalysisRuleRepository extends MongoRepository<PreconditionAnalysisRule, String> {
}
