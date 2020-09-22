package io.segmentme.core.db.service.rule;

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.repository.AbstractAnalysisRuleRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleService extends AbstractDatabaseService<AbstractAnalysisRule<?>, AbstractAnalysisRuleRepository> {

    public List<AbstractAnalysisRule<?>> createAll(List<AbstractAnalysisRule<?>> entity) {
        return repository.saveAll(entity);
    }
}
