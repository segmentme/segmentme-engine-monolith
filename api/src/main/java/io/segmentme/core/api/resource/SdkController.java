package io.segmentme.core.api.resource;

import io.segmentme.core.api.facade.SdkFacade;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.service.dto.analysis.SdkAnalysisRequest;
import io.segmentme.core.service.dto.analysis.SdkAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sdk/synch")
public class SdkController {

    private final SdkFacade sdkFacade;

    @GetMapping("/connect")
    public IntegrationPoint connect(@RequestHeader("integration-point-key") String integrationPointKey) {
        return sdkFacade.connect(integrationPointKey);
    }


    @PostMapping("/analysis/analyze")
    public SdkAnalysisResponse analyze(@RequestHeader("integration-point-key") String integrationPointKey,
                                       @RequestBody SdkAnalysisRequest sdkAnalysisRequest) {
        return sdkFacade.analyze(integrationPointKey, sdkAnalysisRequest);
    }
}
