package io.segmentme.core.db.domain.statistic;

import lombok.Data;

@Data
public class SegmentStatisticCount {
    private String segmentId;
    private int count;
}
