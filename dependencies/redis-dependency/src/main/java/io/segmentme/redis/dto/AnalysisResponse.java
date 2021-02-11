package io.segmentme.redis.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisResponse {

    private String contextId;

    private List<AnalysisResult> analyzedSegments;
}
