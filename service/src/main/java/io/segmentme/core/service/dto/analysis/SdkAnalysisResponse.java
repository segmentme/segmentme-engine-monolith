package io.segmentme.core.service.dto.analysis;

import lombok.Data;

import java.util.List;

@Data
public class SdkAnalysisResponse {
    private String contextId;

    private List<SegmentAnalysisResult> analyzedSegments;
}
