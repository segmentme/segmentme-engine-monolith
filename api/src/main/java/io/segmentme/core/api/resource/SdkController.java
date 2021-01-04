package io.segmentme.core.api.resource;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.api.dto.SdkContextActualizeRequest;
import io.segmentme.core.api.dto.context.ContextSchemaShortInfo;
import io.segmentme.core.api.facade.SdkFacade;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.service.analysis.segment.AnalysisService;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sdk")
public class SdkController {

    private final SdkFacade sdkFacade;

    private final AnalysisService analysisService;

    @GetMapping("/connect")
    public IntegrationPoint connect(@RequestHeader("integration-point-key") String integrationPointKey) {
        return sdkFacade.connect(integrationPointKey);
    }

    @PostMapping("/actualize")
    public ContextSchemaShortInfo actualizeSchema(@RequestHeader("integration-point-key") String integrationPointKey,
                                                  @RequestBody SdkContextActualizeRequest payload) {
        return sdkFacade.actualizeSchema(integrationPointKey, payload);
    }

    @PostMapping("/analysis/analyze")
    public List<SegmentAnalysisResult> analyze(@RequestHeader("integration-point-key") String integrationPointKey,
                                               @RequestParam(required = false) String contextId,
                                               @RequestBody JsonNode payload) {
        return analysisService.analyze(contextId, integrationPointKey, payload);
    }
}
