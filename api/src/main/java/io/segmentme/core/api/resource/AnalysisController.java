package io.segmentme.core.api.resource;

import io.segmentme.core.api.dto.DebugRequest;
import io.segmentme.core.service.analysis.segment.AnalysisService;
import io.segmentme.core.service.analysis.state.StateAnalysisResult;
import io.segmentme.core.service.analysis.state.StateAnalysisService;
import io.segmentme.core.service.converter.SegmentConverter;
import io.segmentme.core.service.dto.analysis.AnalysisData;
import io.segmentme.core.service.dto.analysis.AnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    private final StateAnalysisService stateAnalysisService;


    @PostMapping("/analyze/debug")
    public AnalysisResult analyze(@RequestParam String contextId,
                                  @RequestParam String integrationPointKey,
                                  @RequestBody @Valid DebugRequest request) {
        return analysisService.debug(integrationPointKey, contextId, request.getPayload(), SegmentConverter.of(request.getSegment(), null, integrationPointKey));
    }


    @PostMapping("/state/analyze/{integrationPointKey}")
    public List<StateAnalysisResult> analyze(@PathVariable String integrationPointKey, @RequestBody AnalysisData analysisData) {
        return stateAnalysisService.analyse(integrationPointKey, analysisData);
    }
}
