package io.segmentme.channelservice.dto.channel;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class SdkAnalysisMessageIn extends MessageIn {

    private final MessageType type = MessageType.ANALYSIS_REQUEST;

    @NotEmpty
    private String contextKey;

    @Valid
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
