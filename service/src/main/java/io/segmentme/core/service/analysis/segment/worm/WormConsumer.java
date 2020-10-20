package io.segmentme.core.service.analysis.segment.worm;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import lombok.*;

import java.util.*;
import java.util.function.*;

@NoArgsConstructor
@AllArgsConstructor
public class WormConsumer implements BiConsumer<AbstractCondition, Object>, WormCache {

    private List<BiConsumer<AbstractCondition, Object>> worms = List.of();

    private final Map<String, Boolean> conditionCacheResult = new HashMap<>();

    @Override
    public void accept(AbstractCondition abstractCondition, Object o) {
        worms.forEach(it -> it.accept(abstractCondition, o));
    }

    @Override
    public Boolean findResultInCache(AbstractCondition condition) {
        return conditionCacheResult.get(condition.getHash());
    }

    @Override
    public void addToCache(AbstractCondition condition, boolean result) {
        conditionCacheResult.put(condition.getHash(), result);
    }
}
