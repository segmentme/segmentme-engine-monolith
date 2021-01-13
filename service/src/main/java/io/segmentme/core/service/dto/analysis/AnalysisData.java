package io.segmentme.core.service.dto.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class AnalysisData {
    private JsonNode payload;

    private String clientId;


    public String getPayloadAsString() {
        return this.payload.toString();
    }
}
