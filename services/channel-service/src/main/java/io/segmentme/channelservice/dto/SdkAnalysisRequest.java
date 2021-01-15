package io.segmentme.channelservice.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SdkAnalysisRequest {

    @NotEmpty
    private String contextKey;

    @Valid
    @NotNull
    private AnalysisData analysisData;
}
