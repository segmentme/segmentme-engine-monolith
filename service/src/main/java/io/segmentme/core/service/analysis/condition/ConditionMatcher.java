package io.segmentme.core.service.analysis.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.segment.worm.Worm;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConditionMatcher {

    private final Map<AbstractCondition.ConditionType, Matcher<? extends AbstractCondition>> conditionServices;

    public ConditionMatcher(List<Matcher<? extends AbstractCondition>> services) {
        conditionServices = services.stream().collect(Collectors.toMap(Matcher::getType, it -> it));
    }

    public boolean match(AbstractCondition condition, ContextValueHolder context, Worm<Object> worm) {
        return worm.computeResult(condition.getHash(), matchFunction(condition, context, worm)) == condition.isMatchResult();
    }

    private Function<String, Boolean> matchFunction(AbstractCondition condition, ContextValueHolder context, Worm<Object> worm) {
        return hash -> {
            var matcher = findMatcher(condition.getType());
            var conditionMatchResult = matcher.match(condition, context, worm);
            worm.apply(condition, conditionMatchResult);
            return conditionMatchResult;
        };
    }

    @SuppressWarnings("unchecked")
    private Matcher<AbstractCondition> findMatcher(io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType type) {
        Matcher<? extends AbstractCondition> matcher = conditionServices.computeIfAbsent(type, key -> {
            throw new IllegalStateException("Unknown condition service type " + key);
        });
        return (Matcher<AbstractCondition>) matcher;
    }
}
