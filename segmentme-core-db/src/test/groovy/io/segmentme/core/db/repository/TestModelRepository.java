package io.segmentme.core.db.repository;

import io.segmentme.core.db.models.TestModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestModelRepository extends MongoRepository<TestModel, String> {

}
