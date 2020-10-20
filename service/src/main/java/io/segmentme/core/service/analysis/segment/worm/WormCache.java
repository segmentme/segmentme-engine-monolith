package io.segmentme.core.service.analysis.segment.worm;

import io.segmentme.core.db.domain.condition.AbstractCondition;

public interface WormCache {

    Boolean findResultInCache(AbstractCondition condition);

    void addToCache(AbstractCondition condition, boolean result);
}
