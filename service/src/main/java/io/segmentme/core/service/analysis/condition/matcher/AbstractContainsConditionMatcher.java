package io.segmentme.core.service.analysis.condition.matcher;

import io.segmentme.core.db.domain.condition.ArrayCondition;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.CriteriaValueLocator;
import io.segmentme.core.service.analysis.segment.worm.WormConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractContainsConditionMatcher extends AbstractConditionMatcher<ArrayCondition> {

    @Override
    public boolean match(ArrayCondition condition, ContextValueHolder context, WormConsumer worm) {
        Collection<Comparable<Object>> propertyValue = null;

        try {
            propertyValue = getCollection(condition.getCriteria(), context);
        } catch (ClassCastException ex) {
            log.warn("Unable cast property {} in context {} ,because {}", condition.getCriteria(), context, ex.getMessage());
            Optional.ofNullable(worm).ifPresent(it -> it.accept(condition, ex));
            throw ex;
        } catch (Exception ex) {
            log.warn("Unable to resolve property {} in context {} ,because {}", condition.getCriteria(), context, ex.getMessage());
            Optional.ofNullable(worm).ifPresent(it -> it.accept(condition, ex));
        }

        if (CollectionUtils.isEmpty(propertyValue) && condition.isNullValid()) {
            return true;
        } else if (CollectionUtils.isEmpty(propertyValue) && !condition.isNullValid()) {
            return false;
        }

        Comparable<Object> objectComparable = propertyValue.stream().findFirst().get();

        List<Comparable<Object>> castedConditionValues = condition.getValue()
            .stream()
            .map(conditionValue -> castJsonProperty(conditionValue, objectComparable))
            .collect(Collectors.toList());

        return match(propertyValue, castedConditionValues);
    }

    abstract boolean match(Collection<Comparable<Object>> propertyValue, List<Comparable<Object>> castedConditionValues);

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private Collection<Comparable<Object>> getCollection(String propertyName, ContextValueHolder context) {
        return (Collection<Comparable<Object>>) CriteriaValueLocator.getCriteriaValue(propertyName, context);
    }
}
