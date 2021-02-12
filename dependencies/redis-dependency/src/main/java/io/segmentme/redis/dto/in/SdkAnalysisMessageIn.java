package io.segmentme.redis.dto.in;

import io.segmentme.redis.dto.RedisMessageType;
import io.segmentme.redis.dto.AnalysisRequest;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = true)
public class SdkAnalysisMessageIn extends RedisMessageIn<AnalysisRequest> {

    private final RedisMessageType type = RedisMessageType.ANALYSIS_MESSAGE_IN;

    @NotEmpty
    private String integrationPointKey;

}
