package io.segmentme.core.api.resource;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import io.segmentme.core.service.rule.AnalysisService;
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


    @PostMapping("/analyze")
    public List<SegmentAnalysisResult> analyze(@RequestParam(required = false) String contextId,
                                               @RequestParam String integrationPointKey,
                                               @RequestBody JsonNode payload) {
        return analysisService.analyze(contextId, integrationPointKey, payload);
    }
}
