package io.segmentme.core.db.service.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.repository.AbstractConditionRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConditionService extends AbstractDatabaseService<AbstractCondition<?>, AbstractConditionRepository> {

    public List<AbstractCondition<?>> findByContextId(String contextId) {
        return repository.findByContextIdAndEmbeddedIsFalse(contextId);
    }

    public List<AbstractCondition<?>> createAll(List<AbstractCondition<?>> entities) {
        return repository.saveAll(entities);
    }

    public List<AbstractCondition<?>> updateAll(List<AbstractCondition<?>> entities) {
        return repository.saveAll(entities);
    }

    public void deleteAll(Collection<AbstractCondition<?>> entities) {
        repository.deleteAll(entities);
    }
}
