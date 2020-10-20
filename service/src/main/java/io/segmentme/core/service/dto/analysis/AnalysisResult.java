package io.segmentme.core.service.dto.analysis;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalysisResult {

    private List<SegmentAnalysisResult> segmentAnalysisResults;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, DebugResult>  debugState;

}
