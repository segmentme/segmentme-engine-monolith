package io.segmentme.core.db.service.rule;

import io.segmentme.core.db.domain.rule.Segment;
import io.segmentme.core.db.repository.AbstractAnalysisRuleRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleService extends AbstractDatabaseService<Segment, AbstractAnalysisRuleRepository> {

    public List<Segment> createAll(List<Segment> entity) {
        return repository.saveAll(entity);
    }

    public List<Segment> findByIntegrationPointKey(String integrationPointKey) {
        return repository.findByIntegrationPointKeyAndEmbeddedIsFalse(integrationPointKey);
    }

    public List<Segment> findByContextId(String contextId) {
        return repository.findByContextIdAndEmbeddedIsFalse(contextId);
    }

    public void deleteAll(Collection<Segment> rules) {
        repository.deleteAll(rules);
    }

    public void update(List<Segment> byIntegrationPointKey) {
        repository.saveAll(byIntegrationPointKey);
    }
}
