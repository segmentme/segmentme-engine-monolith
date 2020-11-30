package io.segmentme.core.db.domain.condition;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, exclude = {"value"})
public abstract class SimpleCondition<T> extends AbstractCondition {

    private T value;

    private boolean isNullValid;

    @Override
    public void recalculateHash() {
       super.setHash(String.valueOf(this.hashCode()));
    }
}
