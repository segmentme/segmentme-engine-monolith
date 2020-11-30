package io.segmentme.core.db.service.segment;

import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.db.repository.SegmentRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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

    public Segment findByIntegrationPointKeyAndKey(String integrationPointKey, String key) {
        return repository.findByIntegrationPointKeyAndName(integrationPointKey, key);
    }

    public List<Segment> findByContextId(String contextId) {
        return repository.findByContextId(contextId);
    }

    public void deleteAll(Collection<Segment> rules) {
        repository.deleteAll(rules);
    }

    public void update(List<Segment> byIntegrationPointKey) {
        byIntegrationPointKey.forEach(Segment::recalculateHash);
        repository.saveAll(byIntegrationPointKey);
    }

    @Override
    public Segment create(Segment entity) {
        entity.recalculateHash();
        return super.create(entity);
    }
}
