package io.segmentme.core.service.dto.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class SegmentAnalysisResult {

    private String name;

    private String segmentId;

    private String hash;

    private boolean value;

}
