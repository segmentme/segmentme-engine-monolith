package io.segmentme.core.api.resource;

import io.segmentme.core.api.dto.DashboardData;
import io.segmentme.core.api.facade.WorkspaceFacade;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.service.statistic.StatisticService;
import io.segmentme.core.service.analysis.segment.SegmentManager;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
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
    @GetMapping("/{workspaceId}/total-analysis-count")
    public DashboardData getTotalAnalyticsCount(@PathVariable String workspaceId, @RequestParam int period, @RequestParam(defaultValue = "3") int limit) {
        List<IntegrationPoint> integrationPoints = workspaceFacade.getWorkspaceDetails(workspaceId).getIntegrationPoints();
        return new DashboardData()
            .setAnalysisCount(statisticService.getAnalysisCount(workspaceId, period))
            .setSegmentStatisticCount(statisticService.getSegmentStatistic(workspaceId, period, limit))
            .setSegments(integrationPoints.stream().map(it -> segmentManager.findByIntegrationPointKey(it.getKey())).flatMap(Collection::stream).map(this::toShortSegment).collect(Collectors.toList()))
            .setIntegrationPoints(integrationPoints);
    }

    private DashboardData.SegmentShortInfo toShortSegment(SegmentDto it) {
        return new DashboardData.SegmentShortInfo().setId(it.getId()).setName(it.getName());
    }

}
