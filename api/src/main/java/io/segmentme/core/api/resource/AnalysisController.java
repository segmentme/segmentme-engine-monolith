package io.segmentme.core.api.resource;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.service.analysis.state.StateAnalysisResult;
import io.segmentme.core.service.analysis.state.StateAnalysisService;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import io.segmentme.core.service.analysis.segment.AnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    private final StateAnalysisService stateAnalysisService;

    @PostMapping("/analyze")
    public List<SegmentAnalysisResult> analyze(@RequestParam(required = false) String contextId,
                                               @RequestParam String integrationPointKey,
                                               @RequestBody JsonNode payload) {
        return analysisService.analyze(contextId, integrationPointKey, payload);
    }

    @PostMapping("/state/analyze/{integrationPointKey}")
    public List<StateAnalysisResult> analyze(@PathVariable String integrationPointKey, @RequestBody JsonNode payload) {
        return stateAnalysisService.analyse(integrationPointKey,payload);
    }
}
