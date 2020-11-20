package io.segmentme.core.api.dto;

import io.segmentme.core.db.domain.statistic.AggregatedAnalysisCount;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import lombok.Data;

import java.util.List;

@Data
public class DashboardData {
    private List<IntegrationPoint> integrationPoints;

    private List<SegmentShortInfo> segments;

    private List<AggregatedAnalysisCount> analysisCount;


    @Data
    public static class SegmentShortInfo{
        private String id;
        private String name;
    }
}
