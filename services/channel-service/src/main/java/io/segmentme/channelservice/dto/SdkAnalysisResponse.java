package io.segmentme.channelservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class SdkAnalysisResponse {

    private String contextId;

    private List<SegmentAnalysisResult> analyzedSegments;

    @Data
    public static class SegmentAnalysisResult {

        private String name;

        private String segmentId;

        private String hash;

        private boolean value;

        private long analysisTime;
    }

}
