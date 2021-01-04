package io.segmentme.core.db.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
public class RangeCondition extends SimpleCondition<RangeCondition.RangeValue> {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RangeValue implements Comparable<Object> {

        private Object min;

        private Object max;

        @Override
        public int compareTo(Object o) {

            boolean comparedMin = ((Comparable<Object>) min).compareTo(o) <= 0;
            boolean comparedMax = ((Comparable<Object>) max).compareTo(o) >= 0;

            if (comparedMin && comparedMax) {
                return 0;
            } else if (comparedMin) {
                return 1;
            } else {
                return -1;
            }
        }

    }
}
