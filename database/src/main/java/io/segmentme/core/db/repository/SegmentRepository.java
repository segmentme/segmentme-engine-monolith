package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.rule.Segment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SegmentRepository extends MongoRepository<Segment, String> {

    List<Segment> findByIntegrationPointKey();

    List<Segment> findByIntegrationPointKeyAndEmbeddedIsFalse(String integrationPointKey);

    List<Segment> findByContextIdAndEmbeddedIsFalse(String contextId);
}
