package io.segmentme.core.service.dto.analysis;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SegmentAnalysisResult {

    private String name;

    private String segmentId;

    private String hash;

    private boolean value;

    private long analysisTime;
}
