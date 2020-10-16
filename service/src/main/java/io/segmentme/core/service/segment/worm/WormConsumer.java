package io.segmentme.core.service.segment.worm;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.BiConsumer;

@AllArgsConstructor(staticName = "of")
public class WormConsumer implements BiConsumer<AbstractCondition, Object> {
    private final List<BiConsumer<AbstractCondition, Object>> worms;

    @Override
    public void accept(AbstractCondition abstractCondition, Object o) {
        worms.forEach(it -> it.accept(abstractCondition, o));
    }
}
