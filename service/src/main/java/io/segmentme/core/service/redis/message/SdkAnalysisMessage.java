package io.segmentme.core.service.redis.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.segmentme.core.service.dto.analysis.SdkAnalysisRequest;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SdkAnalysisMessage {

    private String integrationPointKey;

    private String requesterId;

    private SdkAnalysisRequest body;

}
