package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AbstractConditionRepository extends MongoRepository<AbstractCondition<?>, String> {
}
