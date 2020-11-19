package io.segmentme.core.db.domain.statistic;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AggregatedAnalysisCount {
    private LocalDateTime dateTime;
    private int count;
    private String integrationPointKey;

}
