package io.segmentme.core.api.dto;

import io.segmentme.core.service.dto.analysis.segment.SegmentShortInfo;
import io.segmentme.core.service.dto.statistic.StatisticSegmentInfo;
import io.segmentme.core.service.dto.statistic.StatisticShortInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class ExploreListView {

    private StatisticShortInfo statistic;

    private List<StatisticSegmentInfo> segment;

}
