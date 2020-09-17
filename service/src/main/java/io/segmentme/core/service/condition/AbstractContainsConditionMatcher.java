package io.segmentme.core.service.condition;

import io.segmentme.core.db.domain.condition.ArrayCondition;
import io.segmentme.core.db.service.ContextHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractContainsConditionMatcher extends AbstractConditionMatcher<ArrayCondition> {

    @Override
    public boolean match(ArrayCondition condition, ContextHolder context) {
        Collection<Comparable<Object>> propertyValue = null;

        try {
            propertyValue = getCollection(condition.getCriteria(), context);
        } catch (Exception ex) {
            log.info("Unable to resolve property {} in context {}", condition.getCriteria(), context, ex);
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
    private Collection<Comparable<Object>> getCollection(String propertyName, ContextHolder context) {
        return (Collection<Comparable<Object>>) CriteriaValueLocator.getCriteriaValue(propertyName, context);
    }
}
