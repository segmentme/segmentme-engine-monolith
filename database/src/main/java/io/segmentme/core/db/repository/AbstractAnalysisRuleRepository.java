package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AbstractAnalysisRuleRepository extends MongoRepository<AbstractAnalysisRule<?>, String> {

    List<AbstractAnalysisRule<?>> findByPreconditionIdIsNull();

    List<AbstractAnalysisRule<?>> findByIntegrationPointKeyAndEmbeddedIsFalse(String integrationPointKey);
}
