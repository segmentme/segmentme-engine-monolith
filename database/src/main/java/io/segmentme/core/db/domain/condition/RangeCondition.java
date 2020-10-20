package io.segmentme.core.db.domain.condition;

import lombok.*;

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
            Comparable<Object> min = (Comparable<Object>) this.min;
            Comparable<Object> max = (Comparable<Object>) this.max;

            boolean comparedMin = min.compareTo(o) <= 0;
            boolean comparedMax = max.compareTo(o) >= 0;

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
