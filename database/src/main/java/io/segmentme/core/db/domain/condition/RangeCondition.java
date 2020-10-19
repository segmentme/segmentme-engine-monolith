package io.segmentme.core.db.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RangeCondition extends SimpleCondition<RangeCondition.RangeValue> {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RangeValue implements Comparable<Object> {

        private Object min;

        private Object max;


        @Override
        public int compareTo(Object o) {
            return 0;
        }
    }
}
