package io.segmentme.core.api.resource;

import io.segmentme.core.db.domain.statistic.AggregatedAnalysisCount;
import io.segmentme.core.db.service.statistic.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    @GetMapping("/{workspaceId}/total-analysis-count")
    public List<AggregatedAnalysisCount> getTotalAnalyticsCount(@PathVariable String workspaceId, @RequestParam int period) {
        return statisticService.getAnalysisCount(workspaceId, period);
    }

}
