package io.segmentme.core.service.dto.statistic;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class StatisticLogEntry {
    private long analysisTime;


    private String integrationPointKey;

    private ContextValueHolder contextValueHolder;

    private List<Segment> analyzedSegments;

    private List<SegmentAnalysisResult> segmentAnalysisResults;

    private Map<String, ConditionStatisticEntry> conditionResults = new HashMap<>();

    private String workspaceId;

    private JsonNode rawPayload;

    @Data
    public static class ConditionStatisticEntry {
        private boolean result;
        private String criteria;

        private String errors;
    }
}
