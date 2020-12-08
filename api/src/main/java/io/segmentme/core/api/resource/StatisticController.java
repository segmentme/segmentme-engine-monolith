package io.segmentme.core.api.resource;

import io.segmentme.core.api.dto.DashboardData;
import io.segmentme.core.api.dto.ExploreDashboardData;
import io.segmentme.core.api.facade.WorkspaceFacade;
import io.segmentme.core.db.domain.statistic.*;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.service.statistic.StatisticService;
import io.segmentme.core.service.analysis.segment.SegmentManager;
import io.segmentme.core.service.converter.SegmentShortInfoConverter;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import io.segmentme.core.service.dto.analysis.segment.SegmentShortInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;
    private final WorkspaceFacade workspaceFacade;
    private final SegmentManager segmentManager;


    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/segment-statistic-count")
    public List<SegmentStatisticCount> getSegmentStatistic(@PathVariable String workspaceId, @RequestParam int period) {
        return statisticService.getSegmentStatistic(workspaceId, period);
    }


    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/segment-statistic")
    public ExploreDashboardData getStatistics(@PathVariable String workspaceId, @RequestParam int period,
                                            @RequestParam String criteria, @RequestParam String value) {
        var statistics = statisticService.getSegmentStatistic(workspaceId, period, criteria, value);

        List<SegmentShortInfo> segments = segmentManager.findByIds(statistics.stream()
                .map(StatisticLog::getSegmentStatistics)
                .flatMap(Collection::stream)
                .map(StatisticLog.SegmentStatistic::getSegmentId)
                .collect(Collectors.toSet()))
                .stream()
                .map(SegmentShortInfoConverter::of)
                .collect(Collectors.toList());

        return new ExploreDashboardData()
                .setStatistics(statistics)
                .setSegments(segments);
    }

    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/total-analysis-count")
    public DashboardData getTotalAnalyticsCount(@PathVariable String workspaceId, @RequestParam int period) {
        List<IntegrationPoint> integrationPoints = workspaceFacade.getWorkspaceDetails(workspaceId).getIntegrationPoints();
        return new DashboardData()
            .setAnalysisCount(statisticService.getAnalysisCount(workspaceId, period))
            .setSegments(integrationPoints.stream().map(it -> segmentManager.findByIntegrationPointKey(it.getKey())).flatMap(Collection::stream).map(SegmentShortInfoConverter::of).collect(Collectors.toList()))
            .setIntegrationPoints(integrationPoints);
    }
}
