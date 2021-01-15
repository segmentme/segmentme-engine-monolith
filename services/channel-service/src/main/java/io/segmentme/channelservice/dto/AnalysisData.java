package io.segmentme.channelservice.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AnalysisData {

    @NotNull
    private JsonNode payload;

    @NotEmpty
    private String clientId;
}
