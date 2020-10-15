package io.segmentme.core.service.dto.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalysisResult {

    private List<SegmentAnalysisResult> segmentAnalysisResults;

    private Map<String, DebugResult>  debugState;

}
