package io.segmentme.redis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ClientAnalysesStateChanged extends RedisMessage {

    private final RedisMessageType type = RedisMessageType.SEGMENT_CHANGED;

    private String clientId;

    private String integrationPointKey;

    private ChangedAnalysis body;


    @Data
    public static class ChangedAnalysis {
        private List<SegmentAnalysisResult> analyzedSegments;
    }

    @Data
    public static class SegmentAnalysisResult {

        private String name;

        private String segmentId;

        private String hash;

        private boolean value;

        private long analysisTime;
    }

}
