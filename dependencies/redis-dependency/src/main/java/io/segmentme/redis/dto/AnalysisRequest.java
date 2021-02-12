package io.segmentme.redis.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AnalysisRequest {

    private String contextId;

    private String contextKey;

    @NotNull
    private AnalysisData analysisData;

    @Data
    public static class AnalysisData {

        @NotNull
        private JsonNode payload;

        @NotEmpty
        private String clientId;
    }
}
