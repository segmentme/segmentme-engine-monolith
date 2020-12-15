package io.segmentme.core.api.resource;

import io.segmentme.core.api.dto.DashboardData;
import io.segmentme.core.api.dto.ExploreListView;
import io.segmentme.core.api.facade.WorkspaceFacade;
import io.segmentme.core.db.domain.statistic.SegmentStatisticCount;
import io.segmentme.core.db.domain.statistic.StatisticLog;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.service.statistic.StatisticService;
import io.segmentme.core.service.analysis.segment.SegmentManager;
import io.segmentme.core.service.converter.SegmentShortInfoConverter;
import io.segmentme.core.service.converter.StatisticConverter;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import io.segmentme.core.service.dto.statistic.StatisticSegmentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
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
    @GetMapping("/{workspaceId}/segment-statistic-criteria-count")
    public DashboardData getStatisticCriteriaCount(@PathVariable String workspaceId, @RequestParam int period,
                                                   @RequestParam String criteria, @RequestParam String value) {
        List<IntegrationPoint> integrationPoints = workspaceFacade.getWorkspaceDetails(workspaceId).getIntegrationPoints();
        return new DashboardData()
                .setAnalysisCount(statisticService.getAnalysisCountForCriteriaValue(workspaceId, period, criteria, value))
                .setIntegrationPoints(integrationPoints);
    }


    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/segment-statistic-view")
    public Page<ExploreListView> getStatistics(@PathVariable String workspaceId,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                               @RequestParam String criteria, @RequestParam String value, Pageable pageable) {

        var statistics = statisticService.getSegmentStatistic(workspaceId, startDate, endDate, criteria, value, pageable).getContent();

        var segments = segmentManager.findByIds(statistics.stream()
                .map(StatisticLog::getSegmentStatistics)
                .flatMap(Collection::stream)
                .map(StatisticLog.SegmentStatistic::getSegmentId)
                .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(SegmentDto::getId, Function.identity()));

        return statisticService.getSegmentStatistic(workspaceId, startDate, endDate, criteria, value, pageable)
                .map(statistic -> {
                    var statisticSegmentInfos = statistic.getSegmentStatistics().stream()
                            .map(it -> of(segments.get(it.getSegmentId()), it))
                            .collect(Collectors.toList());
                    return ExploreListView.of(StatisticConverter.of(statistic), statisticSegmentInfos);
                });
    }


    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/statistic-log-details/{statisticLogId}")
    public StatisticService.ExploreStatisticLog getStatisticLog(@PathVariable String workspaceId, @PathVariable String statisticLogId) {
        return statisticService.getStatisticLogOverview(statisticLogId);
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


    private StatisticSegmentInfo of(SegmentDto segment, StatisticLog.SegmentStatistic segmentStatistic) {
        return new StatisticSegmentInfo()
                .setId(segment.getId())
                .setName(segment.getName())
                .setAnalysisTime(segmentStatistic.getAnalysisTime())
                .setResult(segmentStatistic.isResult());
    }
}
