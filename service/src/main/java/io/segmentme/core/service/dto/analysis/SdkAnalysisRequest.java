package io.segmentme.core.service.dto.analysis;

import lombok.Data;

@Data
public class SdkAnalysisRequest {
    private String contextId;

    private String contextKey;

    private AnalysisData analysisData;
}
