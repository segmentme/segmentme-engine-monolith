package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AbstractConditionRepository extends MongoRepository<AbstractCondition, String> {

    List<AbstractCondition> findByContextIdAndEmbeddedIsFalse(String id);
}
