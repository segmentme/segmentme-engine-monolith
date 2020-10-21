package io.segmentme.core.service.analysis.segment.worm;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

@AllArgsConstructor(staticName = "of")
public class WormConsumer implements Worm<AbstractCondition> {

    private List<BiConsumer<AbstractCondition, Object>> worms;

    private final Map<String, Boolean> conditionCacheResult = new ConcurrentHashMap<>();

    @Override
    public void apply(AbstractCondition target, Object object) {
        Optional.ofNullable(worms).stream().flatMap(Collection::stream).forEach(it -> it.accept(target, object));
    }

    @Override
    public Boolean computeResult(String key, Function<String, Boolean> matchFunction) {
        return Optional.ofNullable(conditionCacheResult.get(key)).orElseGet(() -> {
            boolean result = matchFunction.apply(key);
            conditionCacheResult.put(key, result);
            return result;
        });
    }
}
