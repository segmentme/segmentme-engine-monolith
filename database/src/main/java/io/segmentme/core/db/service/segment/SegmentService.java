package io.segmentme.core.db.service.segment;

import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.db.repository.SegmentRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentService extends AbstractDatabaseService<Segment, SegmentRepository> {

    public List<Segment> createAll(List<Segment> entity) {
        return repository.saveAll(entity);
    }

    public List<Segment> findByIntegrationPointKey(String integrationPointKey) {
        return repository.findByIntegrationPointKey(integrationPointKey);
    }

    public List<Segment> findByIntegrationPointKeys(Iterable<String> integrationPointKeys) {
        return repository.findByIntegrationPointKeyIn(integrationPointKeys);
    }

    public List<Segment> findByIds(Iterable<String> ids) {
        return StreamSupport.stream(repository.findAllById(ids).spliterator(), false)
            .collect(Collectors.toList());
    }

    public Segment findByIntegrationPointKeyAndKey(String integrationPointKey, String key) {
        return repository.findByIntegrationPointKeyAndName(integrationPointKey, key);
    }

    public List<Segment> findByContextId(String contextId) {
        return repository.findByContextId(contextId);
    }

    public void save(List<Segment> segments) {
        segments.forEach(Segment::recalculateHash);
        repository.saveAll(segments);
    }

    public void save(Segment segment) {
        repository.save(segment);
    }

    @Override
    public Segment create(Segment entity) {
        entity.recalculateHash();
        return super.create(entity);
    }


}
