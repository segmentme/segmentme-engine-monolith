package io.segmentme.core.db.domain.condition;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SimpleCondition<T> extends AbstractCondition {

    private T value;

    private boolean isNullValid;

    private String criteria;

}
