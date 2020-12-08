package io.segmentme.core.api.dto;

import io.segmentme.core.db.domain.statistic.StatisticLog;
import io.segmentme.core.service.dto.analysis.segment.SegmentShortInfo;
import lombok.Data;

import java.util.List;

@Data
public class ExploreDashboardData {

    private List<StatisticLog> statistics;

    private List<SegmentShortInfo> segments;

}
