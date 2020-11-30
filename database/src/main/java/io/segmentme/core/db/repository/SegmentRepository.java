package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.segment.Segment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SegmentRepository extends MongoRepository<Segment, String> {

    List<Segment> findByIntegrationPointKey(String integrationPointKey);

    List<Segment> findByContextId(String contextId);

    Segment findByIntegrationPointKeyAndName(String integrationPointKey, String name);
}
