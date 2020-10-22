package io.segmentme.core.service.analysis.segment.worm;

import java.util.function.Function;

public interface Worm<T> {

    void apply(T target, Object object);

    Boolean computeResult(String key, Function<String, Boolean> matchFunction);
}
