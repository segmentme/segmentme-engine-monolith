package io.segmentme.core.api.resource;

import io.segmentme.core.api.dto.DashboardData;
import io.segmentme.core.api.facade.WorkspaceFacade;
import io.segmentme.core.db.service.statistic.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;
    private final WorkspaceFacade workspaceFacade;

    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/total-analysis-count")
    public DashboardData getTotalAnalyticsCount(@PathVariable String workspaceId, @RequestParam int period) {
        return new DashboardData().setAnalysisCount(statisticService.getAnalysisCount(workspaceId, period)).setIntegrationPoints(workspaceFacade.getWorkspaceDetails(workspaceId).getIntegrationPoints());
    }

}
