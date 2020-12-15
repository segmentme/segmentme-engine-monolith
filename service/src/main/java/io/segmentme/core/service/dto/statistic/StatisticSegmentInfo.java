package io.segmentme.core.service.dto.statistic;

import lombok.Data;

@Data
public class StatisticSegmentInfo {

    private String id;

    private String name;

    private boolean result;

    private long analysisTime;
}
