package io.segmentme.redis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnalysisResult {

    private String name;

    private boolean value;

    private long analysisTime;
}
