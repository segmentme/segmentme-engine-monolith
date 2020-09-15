package io.segmentme.core.db.service.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.context.ContextSchema;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConditionMatcher {

    private final Map<AbstractCondition.ConditionType, Matcher<? extends AbstractCondition<?>>> conditionServices;

    public ConditionMatcher(List<Matcher<? extends AbstractCondition<?>>> services) {
        conditionServices = services.stream().collect(Collectors.toMap(Matcher::getType, it -> it));
    }

    public boolean match(AbstractCondition<?> condition, ContextSchema context) {
        Matcher<AbstractCondition<?>> matcher = findMatcher(condition.getType());
        return matcher.match(condition, context) == condition.isMatchResult();
    }

    @SuppressWarnings("unchecked")
    private Matcher<AbstractCondition<?>> findMatcher(AbstractCondition.ConditionType type) {
        Matcher<? extends AbstractCondition<?>> matcher = conditionServices.computeIfAbsent(type, key -> {
            throw new IllegalStateException("Unknown condition service type " + key);
        });
        return (Matcher<AbstractCondition<?>>) matcher;
    }
}
