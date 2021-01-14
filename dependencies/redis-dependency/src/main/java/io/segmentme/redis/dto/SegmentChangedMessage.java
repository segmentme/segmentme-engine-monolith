package io.segmentme.redis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SegmentChangedMessage extends RedisMessage {
    private String clientId;

    private String integrationPointKey;

    private SdkAnalysisResponse body;

    @Override
    public RedisMessageType type() {
        return RedisMessageType.SEGMENT_CHANGED;
    }

    @Data
    public static class SdkAnalysisResponse {
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
