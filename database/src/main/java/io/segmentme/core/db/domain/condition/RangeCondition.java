package io.segmentme.core.db.domain.condition;

import lombok.*;

public class RangeCondition extends SimpleCondition<RangeCondition.RangeValue> {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RangeValue {

        private Object min;

        private Object max;
    }
}
